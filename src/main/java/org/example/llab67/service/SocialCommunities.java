package org.example.llab67.service;

import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;

import java.util.*;

public class SocialCommunities {
    Service service;

    public SocialCommunities(Service service) {
        this.service = service;
    }

    public int getNumarDeComunitati() {
        Set<Long> visited = new HashSet<>();
        int comunitati = 0;

        for (Utilizator user : service.getUtilizatori()) {
            if (!visited.contains(user.getId())) {
                comunitati++;
                dfs(Optional.of(user), visited, new ArrayList<>());
            }
        }
        return comunitati;
    }

    public List<Utilizator> getCeaMaiSociabilaComunitate() {
        Set<Long> visited = new HashSet<>();
        List<Utilizator> mostSociableCommunity = new ArrayList<>();
        int maxPathLength = 0;

        for (Utilizator user : service.getUtilizatori()) {
            if (!visited.contains(user.getId())) {
                List<Utilizator> currentCommunity = new ArrayList<>();
                int pathLength = dfs(Optional.of(user), visited, currentCommunity);
                if (pathLength > maxPathLength) {
                    maxPathLength = pathLength;
                    mostSociableCommunity = currentCommunity;
                }
            }
        }
        return mostSociableCommunity;
    }

    private int dfs(Optional<Utilizator> user, Set<Long> visited, List<Utilizator> community) {
        if (user.isPresent()) {
            Utilizator utilizator = user.get();
            visited.add(utilizator.getId());
            community.add(utilizator);
            int maxLength = 0;

            for (Prietenie prietenie : service.getPrietenii()) {
                Long friendId = null;
                if (prietenie.getId1().equals(utilizator.getId())) {
                    friendId = prietenie.getId2();
                } else if (prietenie.getId2().equals(utilizator.getId())) {
                    friendId = prietenie.getId1();
                }
                if (friendId != null && !visited.contains(friendId)) {
                    Optional<Utilizator> friend = service.findUtilziator(friendId);
                    if (friend.isPresent()) {
                        maxLength = Math.max(maxLength, 1 + dfs(friend, visited, community));
                    }
                }
            }
            return maxLength;
        }
        return 0;
    }
}
