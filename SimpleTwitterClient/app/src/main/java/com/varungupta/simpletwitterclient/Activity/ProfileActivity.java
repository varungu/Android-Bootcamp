package com.varungupta.simpletwitterclient.Activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.varungupta.simpletwitterclient.Fragments.TweetsListFragment;
import com.varungupta.simpletwitterclient.Model.Tweet;
import com.varungupta.simpletwitterclient.R;
import com.varungupta.simpletwitterclient.TwitterApplication;

public class ProfileActivity extends ActionBarActivity implements TweetsListFragment.ITweetsListFragmentListener{

    long user_id;
    TweetsListFragment tweetsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_id = getIntent().getExtras().getLong("user_id");
        tweetsListFragment = TweetsListFragment.GetInstance(
                new TweetsListFragment.ITweetsGetter() {
                    @Override
                    public void getTweets(long max_id, AsyncHttpResponseHandler handler) {
                        TwitterApplication.getTwitterClient().getUserTimeline(user_id, max_id, handler);
                    }
                },
                this
        );


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flProfile, tweetsListFragment).commit();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 20) {
            if(resultCode == RESULT_OK){
                Tweet tweet = (Tweet)data.getSerializableExtra("tweet");
                tweetsListFragment.add(0, tweet);
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
    public void onProfileClicked(long user_id) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_id", user_id);
        startActivityForResult(intent, 10);

        overridePendingTransition(R.layout.enter_from_right, R.layout.stay_in_place);
    }
}