package tv.vradio.vradiotvserver.stations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@RedisHash("Station")
@RequiredArgsConstructor
@Getter
public class Station {
    private final UUID id;
    private final String ownerUsername;

    private final String name;
    private final Queue<Media> mediaQueue = new ConcurrentLinkedQueue<>();

    @Setter
    private StationStatus status;
}
