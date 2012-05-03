package com.vdm;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class DeadlockActivity extends Activity {
	final Friend alphonse = new Friend("Alphonse");
	final Friend gaston = new Friend("Gaston");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button button1 = (Button) findViewById(R.id.button1);
		// button1.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// new Task2().execute("ss");
		// }
		// });

		new Task().execute(gaston, alphonse);
		new Task().execute(alphonse, gaston);

	}

	private class Task extends AsyncTask<Friend, Integer, String> {

		@Override
		protected String doInBackground(Friend... friend) {
			int i = 0;
			while (i < 20000000) {
				friend[0].bow(friend[1]);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			}
			return "Bowed";
		}
	}

	public static void main(String[] args) {
		DeadlockActivity ac = new DeadlockActivity();
		ac.onCreate(null);
	}

	static class Friend {
		private final String name;

		public Friend(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public synchronized void bow(Friend bower) {
			System.out.format("%s: %s" + "  has bowed to me!%n", this.name,
					bower.getName());
			bower.bowBack(this);
		}

		public synchronized void bowBack(Friend bower) {
			System.out.format("%s: %s" + " has bowed back to me!%n", this.name,
					bower.getName());
		}
	}

}