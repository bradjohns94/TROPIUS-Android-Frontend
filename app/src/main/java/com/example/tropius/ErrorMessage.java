package com.example.tropius;

import java.util.Random;

public class ErrorMessage {
	
	// I should really be doing something better with my time...
	private static final String[] pokequotes =
			{"I like shorts! They're comfy and easy to wear! -- Youngster",
			 "Develop amnesia conveniently and forget everything you heard! -- Team Rocket Grunt",
			 "It's like my Rattata is in thetop percentage of all Rattata! -- Youngster Joey",
			 "Mostly I breath fire, but want to exchange numbers? -- Firebreather Walt",
			 "We need Pokeballs! P-O-K-accent-E balls! -- Barry",
			 "Please make sure the bed is empty before getting in it! -- Sign from Team Galactic HQ",
			 "Like uncooked dough, my Pokemon are sticky! -- Battle Subway Trainer",
			 "These are not shorts! These are half-pants! -- Youngster Kevin",
			 "Pokemon fight? Cool! Rumble! -- Biker",
			 "I'd rather be working! -- Fisherman",
			 "I'm a rambling, gambling dude! -- Gambler",
			 "I'm not into it, but OK! Lets go! -- Youngster",
			 "Wait! You'll have a heart attack! -- Hiker",
			 "I SWALLOW SLUDGE TO TRANSFORM MYSELF! -- Battle Frontier Trainer"};
	
	public static String getErrorMessage() {
		/* Give the user a shameless, ridiculous
		 * pokemon quote...
		 * Please don't sue me, Nintendo...
		 */
		Random rand = new Random();
		int index = rand.nextInt(pokequotes.length);
		return pokequotes[index];
	}
}