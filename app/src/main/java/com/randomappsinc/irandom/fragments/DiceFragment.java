package com.randomappsinc.irandom.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.randomappsinc.irandom.R;
import com.randomappsinc.irandom.activities.MainActivity;
import com.randomappsinc.irandom.constants.RNGType;
import com.randomappsinc.irandom.persistence.HistoryDataManager;
import com.randomappsinc.irandom.persistence.PreferencesManager;
import com.randomappsinc.irandom.utils.RandUtils;
import com.randomappsinc.irandom.utils.ShakeManager;
import com.randomappsinc.irandom.utils.TextUtils;
import com.randomappsinc.irandom.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DiceFragment extends Fragment {

    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.num_dice) EditText numDiceInput;
    @BindView(R.id.num_sides) EditText numSidesInput;
    @BindView(R.id.results_container) View resultsContainer;
    @BindView(R.id.results) TextView results;

    @BindString(R.string.rolls_prefix) String rollsPrefix;
    @BindString(R.string.sum_prefix) String sumPrefix;
    @BindInt(R.integer.shorter_anim_length) int resultsAnimationLength;

    private final TextUtils.SnackbarDisplay snackbarDisplay = new TextUtils.SnackbarDisplay() {
        @Override
        public void showSnackbar(String message) {
            ((MainActivity) getActivity()).showSnackbar(message);
        }
    };

    private final ShakeManager.Listener shakeListener = new ShakeManager.Listener() {
        @Override
        public void onShakeDetected(int currentRngPage) {
            if (currentRngPage == RNGType.DICE) {
                roll();
            }
        }
    };

    private HistoryDataManager historyDataManager;
    private ShakeManager shakeManager = ShakeManager.get();
    private PreferencesManager preferencesManager;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dice_page, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        shakeManager.registerListener(shakeListener);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyDataManager = HistoryDataManager.get(getActivity());
        preferencesManager = new PreferencesManager(getActivity());
        numDiceInput.setText(preferencesManager.getNumDice());
        numSidesInput.setText(preferencesManager.getNumSides());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSettings();
    }

    @OnClick({R.id.num_dice_one, R.id.num_dice_two, R.id.num_dice_three, R.id.num_dice_four})
    public void loadNumDiceQuickOption(TextView view) {
        String value = view.getText().toString();
        numDiceInput.setText(value);
    }

    @OnClick({R.id.num_sides_four, R.id.num_sides_six, R.id.num_sides_eight,
              R.id.num_sides_ten, R.id.num_sides_twelve, R.id.num_sides_twenty})
    public void loadNumSidesQuickOption(TextView view) {
        String value = view.getText().toString();
        numSidesInput.setText(value);
    }

    @OnClick(R.id.roll)
    public void roll() {
        if (verifyForm()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSound(RNGType.DICE);
            }
            int numDice = Integer.parseInt(numDiceInput.getText().toString());
            int numSides = Integer.parseInt(numSidesInput.getText().toString());

            List<Integer> rolls = RandUtils.getNumbers(
                    1,
                    numSides,
                    numDice,
                    false,
                    new ArrayList<Integer>());
            resultsContainer.setVisibility(View.VISIBLE);
            String rollsText = RandUtils.getDiceResults(rolls, rollsPrefix, sumPrefix);
            historyDataManager.addHistoryRecord(RNGType.DICE, rollsText);
            UIUtils.animateResults(results, Html.fromHtml(rollsText), resultsAnimationLength);
        }
    }

    public boolean verifyForm() {
        UIUtils.hideKeyboard(getActivity());
        focalPoint.requestFocus();

        String numSides = numSidesInput.getText().toString();
        String numDice = numDiceInput.getText().toString();
        try {
            if (Integer.parseInt(numSides) <= 0) {
                snackbarDisplay.showSnackbar(getString(R.string.zero_sides));
                return false;
            } else if (Integer.parseInt(numDice) <= 0) {
                snackbarDisplay.showSnackbar(getString(R.string.zero_dice));
                return false;
            }
        } catch (NumberFormatException exception) {
            snackbarDisplay.showSnackbar(getString(R.string.not_a_number));
            return false;
        }
        return true;
    }

    @OnClick(R.id.copy_results)
    public void copyNumbers() {
        String numbersText = results.getText().toString();
        TextUtils.copyResultsToClipboard(numbersText, snackbarDisplay, getActivity());
    }

    private void saveSettings() {
        preferencesManager.saveDiceSettings(
                numSidesInput.getText().toString(),
                numDiceInput.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shakeManager.unregisterListener(shakeListener);
        saveSettings();
        unbinder.unbind();
    }
}
