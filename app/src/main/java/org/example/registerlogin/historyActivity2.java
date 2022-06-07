package org.example.registerlogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class historyActivity2 extends AppCompatActivity {

    Button back_h, update_dtn, del_btn;
    EditText itemText;
    ArrayList<String> arrayList = new ArrayList<>();
    List<MyArrayAdapter> historysaveList;
    DatabaseReference databaseData;
    ListView listView;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private Boolean searchMode = false;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;
   // SingerAdapter sadapter;
//https://bite-sized-learning.tistory.com/213
//https://mailmail.tistory.com/44

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history2);

        Intent intet = getIntent();
        back_h = (Button) findViewById(R.id.back_h);
        databaseData = FirebaseDatabase.getInstance().getReference("Data");
        //itemText = (EditText) findViewById(R.id.itemText);
        historysaveList = new ArrayList<MyArrayAdapter>();
        listView = (ListView) findViewById(R.id.listView);
       // SingerAdapter sadapter=new SingerAdapter();
        del_btn = (Button) findViewById(R.id.del_btn);
        del_btn.setEnabled(false);


//adapter
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                listItems);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //set selected item
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {

                        del_btn.setEnabled(true);
                    }
                });
        addChildEventListener();


        back_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(historyActivity2.this, History.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어간다

            }
        });
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

    }

    private void deleteItem() {

        listView.setItemChecked(selectedPosition, false);
        databaseData.child(listKeys.get(selectedPosition)).removeValue();


    }
    private void addHistory(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        historysave h_save = dataSnapshot.getValue(historysave.class);
        adapter.add(h_save.getDate() + " : " + h_save.getDiary());
    }

    private void removeHistory(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        historysave h_save = dataSnapshot.getValue(historysave.class);
        adapter.remove(h_save.getDate() + " : " + h_save.getDiary());
        listView.setItemChecked(selectedPosition, false);
        databaseData.child(listKeys.get(selectedPosition)).removeValue();

    }
/*   final ArrayAdapter<String> adapter
                    = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
            chat_view.setAdapter(adapter);

            // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
            databaseData.child("Data").child(diary).addChildEventListener(new ChildEventListener() {
 */

        private void addChildEventListener() {
            // 리스트 어댑터 생성 및 세팅
            ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                addHistory(dataSnapshot,adapter);
                /*
                adapter.add(
                        (String) dataSnapshot.child("diary").getValue());

                adapter.add(
                        (String) dataSnapshot.child("date").getValue());



                 */
                listKeys.add(dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                removeHistory(dataSnapshot,adapter);
/*
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);
                int count = adapter.getCount();
                if (index > -1 && index < count) {
                    // adapter.remove(
                    //        (String) dataSnapshot.child("diary").getValue());
                    listItems.remove(index);
                    listView.clearChoices();
                    // listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
                //if (index != -1) {


 */

            }




               /*
                int count, checked;
                count = adapter.getCount();
                if (count > 0) {
                    checked=listView.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        listItems.remove(checked);
                        listView.clearChoices();
                        adapter.notifyDataSetChanged();

                    }

                }

                */

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseData.addChildEventListener(childListener);
    }

    ValueEventListener queryValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

            adapter.clear();
            listKeys.clear();

            while (iterator.hasNext()) {
                DataSnapshot next = (DataSnapshot) iterator.next();

                String match = (String) next.child("description").getValue();
                String key = next.getKey();
                listKeys.add(key);
                adapter.add(match);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}

/*
                if (index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();

                }
  String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);
                //historysave historysave=dataSnapshot.getValue(org.example.registerlogin.historysave.class);

                //int index=listView.getPositionForView(view);
                String str= (String) adapter.getItem(index++);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query taskQuery = ref.child("Data").orderByChild("diary").equalTo(str);
                taskQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        arrayList.remove(listView.getPositionForView(key)); // remove the item from list
                        adapter.notifyDataSetChanged();// notify the changed
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TAG", "onCancelled", databaseError.toException());
                    }
                });
 */

/*
        private void findItems() {

            Query query;

            if (!searchMode) {
                update_dtn.setText("Clear");
                query = databaseData.orderByChild("description").
                        equalTo(itemText.getText().toString());
                searchMode = true;
            } else {
                searchMode = false;
                update_dtn.setText("Find");
                query = databaseData.orderByKey();
            }

            if (itemSelected) {
                listView.setItemChecked(selectedPosition, false);
                itemSelected = false;
                del_btn.setEnabled(false);
            }

            query.addListenerForSingleValueEvent(queryValueListener);
        }

*/

/*
        update_dtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findItems();
            }
        });
*/