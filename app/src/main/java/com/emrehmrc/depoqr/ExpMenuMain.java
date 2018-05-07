package com.emrehmrc.depoqr;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ExpMenuMain extends AppCompatActivity {

    ActionBar ab;
    TextView txtName, txtMail;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Intent i;
    Bundle bundle;
    ImageView imgAvatar;
    TextView txtCountTask;
    String imgPath = "";
    boolean grup1 = true, grup2 = true, grup3 = true;
    String memberID = "";
    ConnectionClass connectionClass;

    CompactCalendarView compactCalendar;
    RecyclerView recyclerView;
    MainTaskAdapter mainTaskAdapter;
    ArrayList<MainTaskModel> datalist;
    ArrayList<String> memberList;
    MainTaskModel gecici;
    HashSet<String> hset;
    private SimpleDateFormat dateDefault = new SimpleDateFormat("yyyy-MM-dd",
            Locale.getDefault());
    SimpleDateFormat dateFormatTask = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
            Locale.getDefault());
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivtyTitle;
    private String[] items;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> lstTitle;
    private Map<String, List<String>> lstChild;
    private ExpNavigationManage navigationManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigaitonexpandable);

        connectionClass = new ConnectionClass();
        ab = getSupportActionBar();
        ab.setTitle("İŞLEM MENÜSÜ");
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.arkaplan));
//Reccle Deneme
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        datalist = new ArrayList<>();

       /* datalist = MainTaskModel.getData();
        mainTaskAdapter = new MainTaskAdapter(getApplicationContext(), datalist);
        recyclerView.setAdapter(mainTaskAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        */
//
        txtCountTask=(TextView)findViewById(R.id.txtCountTasks);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivtyTitle = getTitle().toString();
        expandableListView = (ExpandableListView) findViewById(R.id.navList);
        navigationManage = ExpNavigationManage.getmInstance(this);
        prepareListData();

        View view = getLayoutInflater().inflate(R.layout.header, null, false);
        expandableListView.addHeaderView(view);


//
        expandableListAdapter = new ExpAdapter(ExpMenuMain.this, listDataHeader,
                listDataChild);
        expandableListView.setAdapter(expandableListAdapter);

        // Listview Group click listener
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                /*
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
                        */
                getSupportActionBar().setTitle(listDataHeader.get(groupPosition));
            }
        });

        // Listview Group collasped listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                /*
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
                        */

            }
        });

        // Listview on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /* Toast.makeText( getApplicationContext(),listDataChild.get(listDataHeader.get
                        (groupPosition)).get(childPosition), Toast.LENGTH_SHORT)
                         .show();*/
                switch (listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition)) {

                    case "GRUP BARKOD":
                        i = new Intent(ExpMenuMain.this, GrupAnaBarkod.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }

                        break;
                    case "BARKOD BILGI":
                        i = new Intent(ExpMenuMain.this, BarkodBilgiEkrani.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "DEPO LISTESI":
                        i = new Intent(ExpMenuMain.this, DepoListesi.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "MAL KABUL":
                        i = new Intent(ExpMenuMain.this, MalKabul.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "SEVKİYAT":
                        i = new Intent(ExpMenuMain.this, Sevkiyat.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "İADE":
                        i = new Intent(ExpMenuMain.this, Iade.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "DEPOLAR ARASI TRANSFER":
                        i = new Intent(ExpMenuMain.this, DepoTransfer.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "PLASİYER SATIŞ":
                        i = new Intent(ExpMenuMain.this, PlasiyerList.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            bundle = ActivityOptions.makeSceneTransitionAnimation(ExpMenuMain.this).toBundle();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(i, bundle);
                        }
                        break;
                    case "TAKVİM":

                        break;
                    case "PAYLAŞIM":

                        break;
                    case "SATIN ALMA1":

                        break;
                    case "SATIN ALMA2":

                        break;
                    case "SATIN ALMA3":

                        break;

                    default:
                        break;


                }

                return false;
            }
        });

      /*  expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String selectedItem=((List)lstChild.get(lstTitle.get(groupPosition)))
                        .get(childPosition).toString();
                    ab.setTitle(selectedItem);

                if (items[0].equals(lstTitle.get(groupPosition))){
                    navigationManage.showFragment(selectedItem);

                }

               else if (items[1].equals(lstTitle.get(groupPosition))){
                    navigationManage.showFragment(selectedItem);}

               else if (items[2].equals(lstTitle.get(groupPosition))){ navigationManage.showFragment(selectedItem);}

                else{ throw new IllegalArgumentException("Not Supported Fragment");}

                mDrawerLayout.closeDrawer(GravityCompat.START);


                return false;
            }
        });

*/
        MainTasks mainTasks = new MainTasks();
        String query = "select * from VW_MAINTASKLIST";
        mainTasks.execute(query);
        txtCountTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainTasks mainTasks = new MainTasks();
                String query = "select * from VW_MAINTASKLIST";
                mainTasks.execute(query);
            }
        });


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mActivtyTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mActivtyTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        if (savedInstanceState == null) {

            if (navigationManage != null) {

                String first = listDataHeader.get(0);
                navigationManage.showFragment(first);
                getSupportActionBar().setTitle(first);

            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("DEPOQR");


        mDrawerLayout.openDrawer(GravityCompat.START);
        Date dateNow = new Date();
        compactCalendar = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        SharedPreferences sharedpreferences = getSharedPreferences(AnaSayfa.MyPREFERENCES, Context.MODE_PRIVATE);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtMail = (TextView) view.findViewById(R.id.txtMail);
        imgAvatar = (ImageView) view.findViewById(R.id.memberimg);

        txtName.setText(sharedpreferences.getString("Name", null));
        memberID = sharedpreferences.getString("ID", null);
        txtMail.setText(sharedpreferences.getString("Email", null));
        imgPath = sharedpreferences.getString("MEMBERIMG", "");
        if (!imgPath.equals("")) {
            Picasso.get().load("http://zdijital.com/assets/images/" + imgPath + ".jpg")
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.avatar);
        }



        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+03"));
                long oneday=86400000l;

                MainTasks mainTasks = new MainTasks();
                String q="select * from VW_MAINTASKLIST where CREATINGDATE  >=  '"+sdf.format(new
                        Date(dateClicked.getTime()))+"' and " +
                        "CREATINGDATE  <  '"+sdf.format(new Date(dateClicked.getTime()+oneday))
                        +"'";
                mainTasks.execute(q);



            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                getSupportActionBar().setTitle(sdf.format(firstDayOfNewMonth));
            }
        });


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("PANO");
        listDataHeader.add("LOJİSTİK");
        listDataHeader.add("SATIN ALMA");

        // Adding child data
        //
        List<String> pano = new ArrayList<String>();
        pano.add("TAKVİM");
        pano.add("PAYLAŞIM");

        List<String> lojistik = new ArrayList<String>();
        lojistik.add("GRUP BARKOD");
        lojistik.add("BARKOD BILGI");
        lojistik.add("DEPO LISTESI");
        lojistik.add("MAL KABUL");
        lojistik.add("SEVKİYAT");
        lojistik.add("İADE");
        lojistik.add("DEPOLAR ARASI TRANSFER");
        lojistik.add("PLASİYER SATIŞ");


        List<String> satinalma = new ArrayList<String>();
        satinalma.add("SATIN ALMA1");
        satinalma.add("SATIN ALMA2");
        satinalma.add("SATIN ALMA3");


        if (grup1) listDataChild.put(listDataHeader.get(0), pano); // Header, Child data
        if (grup2) listDataChild.put(listDataHeader.get(1), lojistik);
        if (grup3) listDataChild.put(listDataHeader.get(2), satinalma);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.anasayfa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (id == R.id.exit) {
            finish();
            Intent i = new Intent(ExpMenuMain.this, AnaSayfa.class);
            startActivity(i);

        }
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class MainTasks extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

            memberList = new ArrayList<>();
            hset = new HashSet<>();
            datalist.clear();
            recyclerView.setAdapter(null);

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(String r) {

            mainTaskAdapter = new MainTaskAdapter(getApplicationContext(), datalist);
            recyclerView.setAdapter(mainTaskAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            //  String dateInString = "2018-04-27";
            txtCountTask.setText("GÖREV LİSTESİ("+datalist.size()+")");
            for(int i=0;i<datalist.size()-1;i++){
                Date date = null;
                String dateInString = datalist.get(i).getTaskDate();
                try {
                    date = dateDefault.parse(dateInString);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Event ev1 = new Event(Color.RED, date.getTime(), "TASK");
                compactCalendar.addEvent(ev1);

            }


        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Connection con = connectionClass.CONN("ArGeMerkezi");
                if (con == null) {
                    z = "Bağlantı Hatası";
                } else {



                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(params[0]);

                    while (rs.next()) {

                        gecici = new MainTaskModel();
                        gecici.setTaskId(rs.getString("ID"));
                        gecici.setTaskDescription(rs.getString("DESCRIPTION"));
                        gecici.setTaskCreater(rs.getString("PUBLISHERNAME"));
                        gecici.setTaskDate(rs.getString("CREATINGDATE"));
                        gecici.setTaskTag(rs.getString("NAME"));
                        gecici.setTaskCountMan(rs.getString("TOTALMEMBER").toString());
                        datalist.add(gecici);
                        isSuccess = true;
                    }


                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Hata";
            }

            return z;
        }
    }
}
