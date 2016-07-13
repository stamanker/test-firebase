package t;

import com.firebase.geofire.*;
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
 * rules:
 *      examples:
 *          https://github.com/firebase/geofire-js/tree/master/examples/securityRules
 *      doc:
 *          https://firebase.google.com/docs/database/security/securing-data#predefined_variables
 *          https://firebase.google.com/docs/database/security/
 */
public class TestGeoFire {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        final String databaseUrl = "https://testgeo-8865b.firebaseio.com";
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(new FileInputStream("D:\\work\\test-firebase\\src\\main\\resources\\cred-geo.json"))
                .setDatabaseUrl(databaseUrl)
                .build();

        FirebaseApp.initializeApp(options);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl(databaseUrl);
        GeoFire geoFire = new GeoFire(ref);

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
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(37.7853888, -122.4056), 0.6);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String s, GeoLocation geoLocation) {
                System.out.println("onKeyEntered s = " + s + ", loc = " + geoLocation);
            }

            @Override
            public void onKeyExited(String s) {
                System.out.println("onKeyExited s = " + s);
            }

            @Override
            public void onKeyMoved(String s, GeoLocation geoLocation) {
                System.out.println("onKeyMoved s = " + s + ", loc = " + geoLocation);
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("onGeoQueryReady");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.out.println("onGeoQueryError " + error);
            }
        });

        geoFire.setLocation("firebase-hq2", new GeoLocation(37.7853889, -122.4056972), new GeoFire.CompletionListener() {
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

    }

}
