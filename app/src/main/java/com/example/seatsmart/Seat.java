package com.example.seatsmart;

import android.graphics.Color;
import android.text.SpannableString;
import android.widget.TextView;
import static com.example.utility.DataRef.song;
import static com.example.utility.DataRef.mapSeat;
import static com.example.utility.DataRef.seatClearList;
import static com.example.utility.StringFormatting.getFormattedSeat;
import static com.example.utility.StringFormatting.setTextForGreenOrRed;
import static com.example.utility.StringFormatting.setTextForYellow;

public class Seat extends Facility {

    private SpannableString text;

    public Seat(String seatNo, String status) {
        super(seatNo, status);
    }

    @Override
    public void update() {
        TextView tvCurrentSeat = mapSeat.get(this.id);
        int intSeatNo = Integer.parseInt(this.id.substring(this.id.indexOf('s')+1));
        text = setTextForGreenOrRed(intSeatNo);

        if (status.equals("red")) {
            // set seat to red
            tvCurrentSeat.setBackgroundColor(Color.RED);
            tvCurrentSeat.setText(text);
            checkClear();

        } else if (status.equals("green")) {
            // set seat to green
            tvCurrentSeat.setBackgroundColor(Color.rgb(0, 146, 69));
            tvCurrentSeat.setText(text);
            checkClear();

        } else { // must be yellow
            int timeRemaining = Integer.parseInt(status);

            // set seat to yellow
            tvCurrentSeat.setBackgroundColor(Color.YELLOW);
            text = setTextForYellow(timeRemaining, intSeatNo); // set the status on top of the seat
            tvCurrentSeat.setText(text);

            if (timeRemaining == 0 && !seatClearList.contains(getFormattedSeat(this.id))) {
                seatClearList.add(getFormattedSeat(this.id));
                song.start(); // sound alert to notify librarian to clear seat
            }
        }
    }

    private void checkClear() { // remove seat from seatClearList if cleared by librarian
        if (seatClearList.contains(getFormattedSeat(this.id))) {
            seatClearList.remove(seatClearList.indexOf(getFormattedSeat(this.id)));
        }
    }
}