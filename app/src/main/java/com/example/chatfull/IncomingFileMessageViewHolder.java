package com.example.chatfull;

import android.view.View;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;

public class IncomingFileMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private TextView file_name, time;

    public IncomingFileMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        file_name = itemView.findViewById(R.id.file_name);
        time = itemView.findViewById(R.id.time);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        file_name.setText(message.getFilename());
        time.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
    }

}
