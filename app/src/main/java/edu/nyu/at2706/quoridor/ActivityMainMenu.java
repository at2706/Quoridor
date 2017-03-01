package edu.nyu.at2706.quoridor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.util.ArrayList;

public class ActivityMainMenu extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener, OnTurnBasedMatchUpdateReceivedListener {
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;


    public static GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private boolean mAutoStartSignInFlow = true;

    private TurnBasedMatch mTurnBasedMatch;
    public TurnBasedMatch mMatch;
    public QuoridorTurn mTurnData;
    private GameView gameView;

    private AlertDialog mAlertDialog;
    public boolean isDoingTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mainmenu);
        Log.e("ActivityMainMenu", "onCreate");

        gameView = (GameView) findViewById(R.id.gameView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(AppIndex.API).build();

    }

    @Override
    protected void onStart() {
        Log.e("ActivityMainMenu", "onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("ActivityMainMenu", "onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("Result", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        Log.e("ActivityMainMenu", "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Result", "onConnectionFailed");
    }

    public void onClickMatch(View view) {
        Log.e("ActivityMainMenu", "onClickMatch");
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient,
                1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    public void onClickCheck(View view) {
        Log.e("ActivityMainMenu", "onClickCheck");
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    public void onClickHelp(View view) {
        Log.e("ActivityMainMenu", "onClickHelp");
        Intent intent = new Intent(this, ActivityHelp.class);
        startActivity(intent);
    }

    public void onClickDone(View view) {

        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        mTurnData.turnCounter += 1;

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        mTurnData = null;
    }

    // Rematch dialog
    public void askForRematch() {
        Log.e("ActivityMainMenu", "askForRematch");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.e("ActivityMainMenu", "onInvitationReceived");
        Toast.makeText(
                this,
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onInvitationRemoved(String s) {
        Log.e("ActivityMainMenu", "onInvitationRemoved");
        Toast.makeText(this, "An invitation was removed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {
        Log.e("ActivityMainMenu", "onTurnBasedMatchReceived");
        Toast.makeText(this, "A match was updated.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {
        Log.e("ActivityMainMenu", "onTurnBasedMatchRemoved");
        Toast.makeText(this, "A match was removed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        Log.e("ActivityMainMenu", "onActivityResult");
        super.onActivityResult(request, response, data);
        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Log.e("onActivityResult", getResources().getString(R.string.signin_other_error));
                //BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
            }
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

            Log.d("onActivityResult", "Match = " + match);
        } else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    });
            //showSpinner();
        }
    }

    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        Log.e("ActivityMainMenu", "processResult cancel");
        //dismissSpinner();

        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        isDoingTurn = false;

        showWarning("Match",
                "This match is canceled.  All other players will have their game ended.");
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        Log.e("ActivityMainMenu", "processResult initiate");
        TurnBasedMatch match = result.getMatch();
        //dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }

    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        Log.e("ActivityMainMenu", "processResult leave");
        TurnBasedMatch match = result.getMatch();
        //dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        showWarning("Left", "You've left this match.");
    }

    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        Log.e("ActivityMainMenu", "processResult update");
        TurnBasedMatch match = result.getMatch();
        //dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

        //setViewVisibility();
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        Log.e("ActivityMainMenu", "startMatch");
        mTurnData = new QuoridorTurn();
        // Some basic turn data
        mTurnData.action = "First turn";

        mMatch = match;
        isDoingTurn = true;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);
        Log.e("startMatch", playerId);
        Log.e("startMatch", myParticipantId);

        Intent intent = new Intent(this, ActivityGame.class);
        startActivity(intent);

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        Log.e("ActivityMainMenu", "rematch");
        //showSpinner();
        Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        isDoingTurn = false;
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {
        Log.e("ActivityMainMenu", "getNextParticipantId");
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        Log.e("ActivityMainMenu", "updateMatch");
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = QuoridorTurn.unpersist(mMatch.getData());
                //setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;
    }

    public void showWarning(String title, String message) {
        Log.e("ActivityMainMenu", "showWarning");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {
        Log.e("ActivityMainMenu", "showErrorMessage");
        showWarning("Warning", getResources().getString(stringId));
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        Log.e("ActivityMainMenu", "checkStatusCode");
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d("checkStatusCode", "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    public void setViewVisibility() {
        Log.e("ActivityMainMenu", "setViewVisibility");
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());

        if (!isSignedIn) {
//            findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.mainmenuLayout).setVisibility(View.GONE);
            findViewById(R.id.gameLayout).setVisibility(View.GONE);

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;
        }


//        ((TextView) findViewById(R.id.name_field)).setText(Games.Players.getCurrentPlayer(
//                mGoogleApiClient).getDisplayName());
//        findViewById(R.id.login_layout).setVisibility(View.GONE);

        if (isDoingTurn) {
            findViewById(R.id.mainmenuLayout).setVisibility(View.GONE);
            findViewById(R.id.gameLayout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.mainmenuLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.gameLayout).setVisibility(View.GONE);
        }
    }

}
