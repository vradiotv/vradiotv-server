package tv.vradio.vradiotvserver.stations;

import org.springframework.stereotype.Service;
import tv.vradio.vradiotvserver.account.Account;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StationServiceImpl implements StationService {
    private final Map<String, Station> stations = new ConcurrentHashMap<>();

    @Override
    public Station findStation(Account account) {
        return stations.get(account.getUsername());
    }

    @Override
    public Collection<Station> getAll() {
        return stations.values();
    }

    @Override
    public boolean hasStation(Account account) {
        return stations.containsKey(account.getUsername());
    }

    @Override
    public void registerStation(Station station) {
        stations.put(station.getOwnerUsername(), station);
    }

    @Override
    public void removeStation(Account account) {
        stations.remove(account.getUsername());
    }
}