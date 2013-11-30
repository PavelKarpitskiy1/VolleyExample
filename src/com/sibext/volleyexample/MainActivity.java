package com.sibext.volleyexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends Activity implements OnClickListener
{
	// Right team to left team
	private static final int TO_LEFT_TEAM = 0;
	// Left team to right team
	private static final int TO_RIGHT_TEAM = 1;
	// The score to end the game
	private static final int MAX_SCORE = 3;

	// If true, when a team's strike goes out of the field, the other team will get plus one goal
	private static final boolean OUTS_ARE_COUNTED_AS_GOALS = true;

	// Right team is making first strike
	private int direction = TO_LEFT_TEAM;

	private static final String LEFT_SECTOR = "left";
	private static final String RIGHT_SECTOR = "right";
	private static final String CENTER_SECTOR = "center";
	private static final String GRID_SECTOR = "near grid";

	private ViewGroup resultView;
	private TextView currentStrike;
	private TextView textViewLeftTeamScore;
	private TextView textViewRightTeamScore;
	private Button buttonSend;
	private Button buttonReset;

	private int leftTeamScore;
	private int rightTeamScore;
	private boolean gameOver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		VolleyHelper.init(this);

		resultView = (ViewGroup) findViewById(R.id.result);
		textViewLeftTeamScore = (TextView) findViewById(R.id.left_team_score);
		textViewRightTeamScore = (TextView) findViewById(R.id.right_team_score);
		buttonSend = (Button) findViewById(R.id.button_send);
		buttonSend.setOnClickListener(this);
		buttonReset = (Button) findViewById(R.id.button_reset);
		buttonReset.setOnClickListener(this);
	}

	@Override
	public void onClick(final View view)
	{
		switch (view.getId())
		{
			case R.id.button_send:

				if (gameOver)
				{
					Toast.makeText(this, "The game is over!", Toast.LENGTH_SHORT).show();
					return;
				}

				buttonSend.setClickable(false);
				buttonSend.setEnabled(false);

				final RequestQueue queue = VolleyHelper.getRequestQueue();
				final JSONObject request = new JSONObject();

				try
				{
					request.put("direction", direction);
				}
				catch (final JSONException e)
				{
					e.printStackTrace();
				}

				final JsonObjectRequest volleyRequest = new JsonObjectRequest(Method.POST, "http://volley.sibext.com/", request, createMyReqSuccessListener(), createMyReqErrorListener());

				queue.add(volleyRequest);

				currentStrike = new TextView(MainActivity.this);
				currentStrike.setText("-> " + direction);
				resultView.addView(currentStrike);

				break;

			case R.id.button_reset:

				resetGame();
				break;
		}
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener()
	{
		return new CustomResponseListener();
	}

	private Response.ErrorListener createMyReqErrorListener()
	{
		return new CustomResponseListener();
	}

	private class CustomResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onResponse(final JSONObject response)
		{
			String strikeResult;
			String strikePlace;
			int newDirection;

			try
			{
				strikeResult = response.getString("result");
				strikePlace = response.getString("place");
				newDirection = response.getInt("direction");
			}
			catch (final JSONException e)
			{
				Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_LONG).show();
				return;
			}

			if (strikeResult.equalsIgnoreCase("goal"))
			{
				if (direction == TO_LEFT_TEAM)
				{
					rightTeamScore++;
					textViewRightTeamScore.setText(String.valueOf(rightTeamScore));
				}
				else
				{
					leftTeamScore++;
					textViewLeftTeamScore.setText(String.valueOf(leftTeamScore));
				}
			}
			else if (OUTS_ARE_COUNTED_AS_GOALS && strikeResult.equalsIgnoreCase("out"))
			{
				if (direction == TO_LEFT_TEAM)
				{
					leftTeamScore++;
					textViewLeftTeamScore.setText(String.valueOf(leftTeamScore));
				}
				else
				{
					rightTeamScore++;
					textViewRightTeamScore.setText(String.valueOf(rightTeamScore));
				}
			}

			clearAllSectors();

			if (direction == TO_LEFT_TEAM)
			{
				// TODO : draw ball on left part of field, using strikePlace
				// textView.setBackgroundDrawable(R.drawable.volley_ball);

				if (strikePlace.equalsIgnoreCase(LEFT_SECTOR))
				{
				    // left
				}
				else if (strikePlace.equalsIgnoreCase(RIGHT_SECTOR))
				{
					// right
				}
				else if (strikePlace.equalsIgnoreCase(CENTER_SECTOR))
				{
					// center
				}
				else
				{
					// near grid
				}
			}
			else
			{
				// TODO : draw ball on right part of field, using strikePlace
			}

			direction = newDirection;

			if (leftTeamScore >= MAX_SCORE || rightTeamScore >= MAX_SCORE)
			{
				setGameOver();
			}

			currentStrike.setText(currentStrike.getText() + " (" + strikeResult + "), " + strikePlace);

			buttonSend.setClickable(true);
			buttonSend.setEnabled(true);
		}

		@Override
		public void onErrorResponse(final VolleyError error)
		{
			Toast.makeText(MainActivity.this, "Error = " + error.getMessage(), Toast.LENGTH_LONG).show();

			buttonSend.setClickable(true);
			buttonSend.setEnabled(true);
		}
	}

	private void clearAllSectors()
	{
		// TODO : clear backgrounds of all 8 sectors
		// textView.setBackgroundDrawable(null);
	}

	private void setGameOver()
	{
		gameOver = true;

		final String winner = leftTeamScore == MAX_SCORE ? "left" : "right";

		Toast.makeText(this, "The winner is " + winner + " team!", Toast.LENGTH_SHORT).show();
	}

	private void resetGame()
	{
		leftTeamScore = 0;
		rightTeamScore = 0;
		textViewLeftTeamScore.setText(String.valueOf(leftTeamScore));
		textViewRightTeamScore.setText(String.valueOf(rightTeamScore));

		resultView.removeAllViews();
		clearAllSectors();
		gameOver = false;
	}
}
