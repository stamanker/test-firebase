package t;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * auth from here: https://developers.google.com/identity/protocols/application-default-credentials
 * Date: 13.07.2016
 */
public class M {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(new FileInputStream("D:\\work\\test-firebase\\src\\main\\resources\\cred.json"))
                .setDatabaseUrl("https://test-91459.firebaseio.com")
                .build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
        System.out.println("firebaseApp = " + firebaseApp);

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        DatabaseReference ref = FirebaseDatabase
            .getInstance()
            .getReference("dddd");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                System.out.println("data = " + document);
            }

            public void onCancelled(DatabaseError databaseError) {
                System.err.println("databaseError = " + databaseError);
            }
        });
        Thread.sleep(5000);
    }

}
