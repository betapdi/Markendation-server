package com.markendation.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.markendation.server.auth.entities.User;
import com.markendation.server.auth.repositories.UserRepository;
import com.markendation.server.classes.Location;
import com.markendation.server.dto.UserDto;
import com.markendation.server.exceptions.UserNotFoundException;
import com.markendation.server.models.Store;
import com.markendation.server.repositories.metadata.StoreRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public UserService(UserRepository userRepository, StoreRepository storeRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    public UserDto getInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        UserDto response = new UserDto();
        response.update(user);

        return response;
    }

    @Async
    public void updateNearStore(User user) {
        List<Store> stores = storeRepository.findAll();
        Map<String, Integer> cntChain = new HashMap<>();
        
        double radius = 5;
        int storeCnt = 0;

        for (Store store : stores) {
            String chain = store.getChain();
            int num = (cntChain.get(chain) != null) ? cntChain.get(chain) : 0;
            if (num == 5) continue;

            if (store.getLocation() == null) System.out.println(store);

            double lon1 = user.getLocation().getLongitude(), lat1 = user.getLocation().getLatitude();
            double lon2 = Double.parseDouble(store.getLocation().getCoordinates().get(0));
            double lat2 = Double.parseDouble(store.getLocation().getCoordinates().get(1));

            if (CalculatingService.calculateDistance(lat1, lon1, lat2, lon2) <= radius) {
                double routeDistance = CalculatingService.getRouteDistance(lon1, lat1, lon2, lat2);
                if (routeDistance == -1) routeDistance = radius;

                ++storeCnt;
                cntChain.put(store.getChain(), num + 1);
                
                user.getNearStores().add(Pair.of(store.getId(), routeDistance));
                if (storeCnt % 3 == 0) user = userRepository.save(user);
            }
        }

        user = userRepository.save(user);
    }

    public void updateLocation(String email, Location location) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException());
        user.setLocation(location);
        user.setNearStores(new ArrayList<>());
        User savedUser = userRepository.save(user);

        updateNearStore(savedUser);
    }
}
