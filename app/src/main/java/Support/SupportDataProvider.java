package Support;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Models.Appeal;

public class SupportDataProvider {
    private FirebaseDatabase db;
    private DatabaseReference appeals;
    public SupportDataProvider(){
        db=FirebaseDatabase.getInstance();
        appeals=db.getReference().child("SupportAppeals");
    }
    private static SupportDataProvider provider = new SupportDataProvider();
    public static SupportDataProvider getInstance(){
        return provider;
    }
    public void addAppeal(Appeal appeal, Context context){
        appeals.child(appeal.getTitle()).setValue(appeal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Appeal sent successfully!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Something wrong happened, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void addReport(Appeal appeal, Context context){
        appeals.child("Reports").child(appeal.getTitle()).setValue(appeal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Report sent successfully!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Something wrong happened, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
