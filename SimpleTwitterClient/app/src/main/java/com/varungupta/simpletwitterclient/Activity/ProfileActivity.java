package com.varungupta.simpletwitterclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.varungupta.simpletwitterclient.Fragments.ProfileViewFragment;
import com.varungupta.simpletwitterclient.Fragments.TweetsListFragment;
import com.varungupta.simpletwitterclient.Model.Tweet;
import com.varungupta.simpletwitterclient.Model.User;
import com.varungupta.simpletwitterclient.R;
import com.varungupta.simpletwitterclient.TwitterApplication;

public class ProfileActivity extends ActionBarActivity implements TweetsListFragment.ITweetsListFragmentListener{

    User user;
    ProfileViewFragment profileViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
                overridePendingTransition(R.layout.stay_in_place, R.layout.exit_to_right);
            }
        });

        user = (User)getIntent().getExtras().getSerializable("user");
        profileViewFragment = ProfileViewFragment.GetInstance(
                user,
                new ProfileViewFragment.ITweetsGetter() {
                    @Override
                    public void getTweets(long max_id, AsyncHttpResponseHandler handler) {
                        TwitterApplication.getTwitterClient().getUserTimeline(user.id, max_id, handler);
                    }
                },
                this
        );

        TextView tv_compose_actionbar_char_count = (TextView) findViewById(R.id.tv_compose_actionbar_char_count);
        tv_compose_actionbar_char_count.setText(user.name);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flProfile, profileViewFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_compose) {
            startComposeActivity("", 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 20) {
            if(resultCode == RESULT_OK){
                Tweet tweet = (Tweet)data.getSerializableExtra("tweet");
                profileViewFragment.add(0, tweet);
            }
        }
    }

    @Override
    public void onReplyClicked(String usersInfo, long in_reply_to_status_id) {
        startComposeActivity(usersInfo, in_reply_to_status_id);
    }

    private void startComposeActivity(String usersInfo, long in_reply_to_status_id) {
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.putExtra("info", usersInfo);
        intent.putExtra("in_reply_to_status_id", in_reply_to_status_id);
        startActivityForResult(intent, 20);

        overridePendingTransition(R.layout.enter_from_bottom, R.layout.stay_in_place);
    }

    @Override
    public void onProfileClicked(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivityForResult(intent, 10);

        overridePendingTransition(R.layout.enter_from_right, R.layout.stay_in_place);
    }
}
