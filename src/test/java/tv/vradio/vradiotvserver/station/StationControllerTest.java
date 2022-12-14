package tv.vradio.vradiotvserver.station;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import tv.vradio.vradiotvserver.RedisExtension;
import tv.vradio.vradiotvserver.account.AccountController;
import tv.vradio.vradiotvserver.account.TestAccountData;
import tv.vradio.vradiotvserver.account.auth.AuthToken;
import tv.vradio.vradiotvserver.stations.Media;
import tv.vradio.vradiotvserver.stations.Station;
import tv.vradio.vradiotvserver.stations.StationController;

import java.util.UUID;

@SpringBootTest
@ExtendWith(RedisExtension.class)
@DirtiesContext
@Order(3)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StationControllerTest {
    private static final String TEST_STATION_NAME = "Test";

    private static final String TEST_MEDIA_NAME = "test_";
    private static final String TEST_MEDIA_URL = "https://google.com/";

    @Autowired
    private StationController stationController;
    @Autowired
    private AccountController accountController;
    @Autowired
    private RedisTemplate<String, String> redis;

    private static UUID stationId;
    private static AuthToken token;

    @Test
    @Order(1)
    public void createStationTest() {
        // Create an account with pre-set values
        accountController.createAccount(TestAccountData.USERNAME, TestAccountData.EMAIL, TestAccountData.PASSWORD);
        // Generate token for said account
        token = accountController.login(TestAccountData.USERNAME, TestAccountData.PASSWORD);

        // Create a station with pre-set values
        Station station = stationController.create(token.id().toString(), TEST_STATION_NAME);

        // Ensure the station owner equals the token owner
        assertEquals(station.getOwnerUsername(), token.accountName());

        // Get the station from controller
        Station compare = stationController.get(station.getId().toString());
        // Ensure the controller returns the same value
        assertEquals(station.getName(), compare.getName());

        // save the station for use with other tests
        stationId = station.getId();
    }

    @Test
    @Order(2)
    public void getByOwnerTest() {
        // Ensure station was created in previous test
        assertNotNull(stationId);

        // get station by the owner
        Station byName = stationController.getByOwner(token.accountName());
        // Ensure the id equals the known id
        assertEquals(byName.getId(), stationId);
    }

    @Test
    @Order(2)
    public void addMediaTest() {
        // Create two media with known values
        Media test1 = stationController.queueMedia(stationId.toString(), token.id().toString(), TEST_MEDIA_URL, TEST_MEDIA_NAME + 1, Media.StreamingService.NETFLIX, 0L);
        Media test2 = stationController.queueMedia(stationId.toString(), token.id().toString(), TEST_MEDIA_URL, TEST_MEDIA_NAME + 2, Media.StreamingService.NETFLIX, 0L);

        // Get the station from the controller
        Station station = stationController.get(stationId.toString());

        // Ensure the size of queue is 2
        assertEquals(station.getMediaQueue().size(), 2);

        String compare1 = station.getMediaQueue().get(0).name();
        String compare2 = station.getMediaQueue().get(1).name();

        // Ensure the media which was added is present in the queue
        assertTrue(compare1.equals(test1.name()) || compare1.equals(test2.name()));
        assertTrue(compare2.equals(test1.name()) || compare2.equals(test2.name()));
    }

    @Test
    @Order(2)
    public void joinCodeTest () {
        // Generate a join code
        int joinCode = stationController.join(stationId.toString());
        // Get station from redis
        String fromRedis = redis.opsForValue().get("join-code:" + joinCode);

        // Ensure join code is present in redis
        assertEquals(stationId.toString(), fromRedis);
    }
}
