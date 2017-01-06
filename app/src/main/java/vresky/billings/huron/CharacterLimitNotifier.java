package vresky.billings.huron;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by Matt on 06/01/2017.
 */
public class CharacterLimitNotifier implements TextWatcher {
    private int charLimit;
    private int charsRemaining;
    private int oldStringLength = -1;
    private int newStringLength = -1;

    private Context context;
    private TextView tv;

    public CharacterLimitNotifier(Context context, int charLimit, TextView charLimitTv) {
        this.charLimit = charLimit;
        this.charsRemaining = charLimit;
        this.tv = charLimitTv;
        this.context = context;
    }

    // the count characters at start are about to be replaced by new text with length after
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        oldStringLength = s.length();
    }

    // the count characters beginning at start have just replaced old text that had length before
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        newStringLength = s.length();
        charsRemaining -= (newStringLength - oldStringLength);
        tv.setText(context.getResources().getString(R.string.characters_remaining_message,
                charsRemaining));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}