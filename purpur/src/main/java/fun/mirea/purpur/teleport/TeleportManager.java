package fun.mirea.purpur.teleport;

import fun.mirea.purpur.utility.timer.TeleportTimer;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class TeleportManager {

    private final Map<String, Collection<TeleportRequest>> requests;

    public TeleportManager() {
        requests = new HashMap<>();
    }

    public boolean addRequest(Player requester, Player target) {
        if (!requests.containsKey(requester.getName()))
            requests.put(requester.getName(), new ArrayList<>());
        Iterator<TeleportRequest> requestIterator = requests.get(requester.getName()).iterator();
        while (requestIterator.hasNext()) {
            TeleportRequest request = requestIterator.next();
            if (request.isExpired()) {
                requestIterator.remove();
            } else if (request.getTarget().equals(target.getName())) {
                return false;
            }
        }
        TeleportTimer timer = new TeleportTimer(requester, target.getLocation()) {
            @Override
            public void teleportCallback() {
                Player onlineTarget = Bukkit.getPlayerExact(target.getName());
                if (onlineTarget != null)
                    requester.teleport(onlineTarget.getLocation());
                requester.playSound(requester.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                requester.getWorld().spawnParticle(Particle.PORTAL, requester.getLocation(), 1);
                if (requests.containsKey(requester.getName())) {
                    requests.get(requester.getName()).removeIf(request -> request.getTarget().equals(target.getName()));
                    if (requests.get(requester.getName()).isEmpty()) requests.remove(requester.getName());
                }
            }
            @Override
            public void cancelCallback() {
                if (requests.containsKey(requester.getName())) {
                    requests.get(requester.getName()).removeIf(request -> request.getTarget().equals(target.getName()));
                    if (requests.get(requester.getName()).isEmpty()) requests.remove(requester.getName());
                }
            }
        };
        TeleportRequest teleportRequest = new TeleportRequest(target.getName(), timer);
        requests.get(requester.getName()).add(teleportRequest);
        return true;
    }

    public boolean acceptRequest(Player requester, Player target) {
        if (requests.containsKey(requester.getName())) {
            Iterator<TeleportRequest> requestIterator = requests.get(requester.getName()).iterator();
            while (requestIterator.hasNext()) {
                TeleportRequest request = requestIterator.next();
                if (request.isExpired()) {
                    requestIterator.remove();
                } else if (request.getTarget().equals(target.getName())) {
                    request.getTimer().start(requester.isOp());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean denyRequest(Player requester, Player target) {
        if (requests.containsKey(requester.getName())) {
            Iterator<TeleportRequest> requestIterator = requests.get(requester.getName()).iterator();
            while (requestIterator.hasNext()) {
                TeleportRequest request = requestIterator.next();
                if (request.isExpired()) {
                    requestIterator.remove();
                } else if (request.getTarget().equals(target.getName())) {
                    requestIterator.remove();
                    return true;
                }
            }
        }
        return false;
    }
}
