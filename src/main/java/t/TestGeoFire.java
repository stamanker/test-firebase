package t;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

/**
 * https://github.com/firebase/geofire-java
 */
public class TestGeoFire {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(new FileInputStream("D:\\work\\test-firebase\\src\\main\\resources\\cred.json"))
                .setDatabaseUrl("https://test-91459.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://test-91459.firebaseio.com");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation("firebase-hq", new GeoLocation(37.7853889, -122.4056973), new GeoFire.CompletionListener() {
// FROM example - unexpected argument type 'FirebaseError'
//            @Override
//            public void onComplete(String key, FirebaseError error) {
//                if (error != null) {
//                    System.err.println("There was an error saving the location to GeoFire: " + error);
//                } else {
//                    System.out.println("Location saved on server successfully!");
//                }
//            }

            @Override
            public void onComplete(String s, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }

            }
        });

        CountDownLatch latch = new CountDownLatch(1);
        geoFire.getLocation("firebase-hq", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                latch.countDown();
                if (location != null) {
                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latch.countDown();
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });
        latch.await();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(37.7832, -122.4056), 0.6);
    }

}
