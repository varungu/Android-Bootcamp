package varungu.instagramclient;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by varungupta on 5/4/2015.
 */
public class InstagramPhotosAdapter  extends ArrayAdapter<InstagramPhoto>{

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, R.layout.item_photo, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InstagramPhoto photo = getItem(position);
        InstagramPhotoViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);

            viewHolder = new InstagramPhotoViewHolder();
            viewHolder.ivProfilePhoto = (ImageView) convertView.findViewById(R.id.ivProfilePhoto);
            viewHolder.tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tvTime);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvLikesCount = (TextView)convertView.findViewById(R.id.tvLikesCount);
            viewHolder.tvCaption = (TextView)convertView.findViewById(R.id.tvCaption);
            viewHolder.tvCommentsTitle = (TextView)convertView.findViewById(R.id.tvCommentsTitle);
            viewHolder.tvComment1 = (TextView)convertView.findViewById(R.id.tvComment1);
            viewHolder.tvComment2 = (TextView)convertView.findViewById(R.id.tvComment2);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (InstagramPhotoViewHolder)convertView.getTag();
        }

        // Load profile photo
        // ivProfilePhoto.setImageResource(0) will clear the last image in case convertView is reused
        viewHolder.ivProfilePhoto.setImageResource(0);
        Picasso.with(getContext()).load(photo.profilePhotoUrl).into(viewHolder.ivProfilePhoto);

        // Set username
        viewHolder.tvUsername.setText(photo.username);

        // Convert CreatedTime to Relative time and set the same
        viewHolder.tvTime.setText(getRelativeTime(photo.createdTime));

        // Set image
        viewHolder.ivImage.setImageResource(0);
        Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.loader).into(viewHolder.ivImage);

        if (photo.type.equals("image")){
            viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    // Start NewActivity.class
                    Intent photoIntent = new Intent(getContext(), PhotoActivity.class);
                    photoIntent.putExtra("url", photo.imageUrl);
                    getContext().startActivity(photoIntent);
                }
            });
        }
        else {
            viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    // Start NewActivity.class
                    Intent videoIntent = new Intent(getContext(), VideoViewActivity.class);
                    videoIntent.putExtra("url", photo.videoUrl);
                    videoIntent.putExtra("caption", photo.caption);
                    getContext().startActivity(videoIntent);
                }
            });
        }

        // Set likes count
        DecimalFormat numberFormatter = new DecimalFormat("###,###");
        viewHolder.tvLikesCount.setText(String.format("%s likes", numberFormatter.format(photo.likesCount)));


        // Set caption
        String caption = String.format("<font color=#41739C>%s</font> <font color=#000000>%s</font>", photo.captionUsername, photo.caption);
        viewHolder.tvCaption.setText(Html.fromHtml(caption));


        // Set comments
        viewHolder.tvCommentsTitle.setText(String.format("view all %s comments", numberFormatter.format(photo.commentsCount)));
        viewHolder.tvCommentsTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent commentIntent = new Intent(getContext(), CommentsActivity.class);
                commentIntent.putExtra("id", photo.id);
                commentIntent.putExtra("username", photo.username);
                commentIntent.putExtra("profile", photo.profilePhotoUrl);
                commentIntent.putExtra("caption", photo.caption);

                getContext().startActivity(commentIntent);
            }
        });

        addComment(viewHolder.tvComment1, photo.comment1User, photo.comment1);
        addComment(viewHolder.tvComment2, photo.comment2User, photo.comment2);

        return convertView;
    }

    private void addComment(TextView textView, String user, String comment)
    {
        String commentHtml = String.format("<font color=#41739C>%s</font> <font color=#000000>%s</font>", user, comment);
        textView.setText(Html.fromHtml(commentHtml));
    }

    private String getRelativeTime(Date time)
    {
        Date currentTime = new Date();
        long timeDiff = (currentTime.getTime() - time.getTime())/1000; // Time difference in seconds

        // Convert seconds to string
        long minute = 60;
        long hour = minute * 60;
        long day = hour * 24;
        long week = day * 7;

        if (timeDiff < minute){
            return String.format("%ds", timeDiff);
        }
        else if (timeDiff < hour){
            return String.format("%dm", timeDiff/minute);
        }
        if (timeDiff < day){
            return String.format("%dh", timeDiff/hour);
        }
        if (timeDiff < week){
            return String.format("%dm", timeDiff/day);
        }
        return String.format("%dw", timeDiff/week);
    }
}
