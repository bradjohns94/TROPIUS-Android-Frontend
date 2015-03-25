package com.example.tropius;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SongSelectorSpinner {
	
	// The JSONObject returned from TROPIUS representing the music library
	private JSONObject library;
	// The spinners themselves
	private Spinner artistSpinner;
	private Spinner albumSpinner;
	private Spinner songSpinner;
	// The controlling activity to add the spinners to
	private Activity controller;
	// The lists that populate the spinners
	private ArrayList<String> artists;
	private ArrayList<String> albums;
	private ArrayList<String> songs;
	// A dictionary mapping the spinner name to the row that was added
	private HashMap<String, TableRow> added;
	// The parameters specifying what to draw the spinners on and where to start
	private TableLayout table;
	private int startIndex;
	// Values to represent which spinners have been selected
	private String selected[]; // In accordance with the "type" constants as indexes, indexes are selected text
	private int mostSignificant; // In accordance with the "type" constants
	// Values that exist to make sure the spinners only repopulate when necessary
	private boolean inProgress;
	
	// Some constant values used for clarity
	private static final String ARTIST_DEFAULT = "Select an Artist:";
	private static final String ALBUM_DEFAULT = "Select an Album:";
	private static final String SONG_DEFAULT = "Select a Song:";
	private static final int DEFAULT_TYPE = -1; // When there is no selection
	private static final int ARTIST_TYPE = 0;
	private static final int ALBUM_TYPE = 1;
	private static final int SONG_TYPE = 2;
	// Some constant values reused as table row parameters
	private final TableRow.LayoutParams textParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, (float)0.45);
	private final TableRow.LayoutParams spinnerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, (float)0.55);
	
	
	public SongSelectorSpinner(Activity controller, JSONObject library, TableLayout table, int startIndex) {
		this.controller = controller;
		this.library = library;
		this.table = table;
		this.startIndex = startIndex;
		this.mostSignificant = DEFAULT_TYPE; // Nothing selected yet
		this.selected = new String[3];
		// Initialize the selected strings to defaults
		this.selected[ARTIST_TYPE] = ARTIST_DEFAULT;
		this.selected[ALBUM_TYPE] = ALBUM_DEFAULT;
		this.selected[SONG_TYPE] = SONG_DEFAULT;
		artistSpinner = new Spinner(controller);
		albumSpinner = new Spinner(controller);
		songSpinner = new Spinner(controller);
		artists = new ArrayList<String>();
		albums = new ArrayList<String>();
		songs = new ArrayList<String>();
		added = new HashMap<String, TableRow>();
		inProgress = false;
		initLibrary();
		initSpinners();
	}
	
	private void initLibrary() {
		// Generate the artist/album/song ArrayLists
		artists = new ArrayList<String>();
		albums = new ArrayList<String>();
		songs = new ArrayList<String>();
		// Add a prompt as the first item (also used for backtracing)
		artists.add(ARTIST_DEFAULT);
		albums.add(ALBUM_DEFAULT);
		songs.add(SONG_DEFAULT);
		// Iterate through the library to populate each list
		Iterator<String> artistIterator = library.keys();
		try {
			while (artistIterator.hasNext()) {
				String artist = artistIterator.next();
				artists.add(artist);
				Iterator<String> albumIterator = library.getJSONObject(artist).keys();
				while (albumIterator.hasNext()) {
					String album = albumIterator.next();
					albums.add(album);
					JSONArray songList = library.getJSONObject(artist).getJSONArray(album);
					for (int i = 0; i < songList.length(); i++) {
						String song = songList.getString(i);
						songs.add(song);
					}
				}
			}
		} catch (JSONException e) {
			// TODO error handling
		}
	}
	
	private void initSpinners() {
		// Create the artist spinner
		ArrayAdapter<String> artistAdapter 
		= new ArrayAdapter<String>(controller,
				android.R.layout.simple_spinner_dropdown_item,
				artists);
		artistSpinner.setAdapter(artistAdapter);
		artistSpinner.setLayoutParams(spinnerParams);
		artistSpinner.setOnItemSelectedListener(new ArtistSpinnerListener());
		// Create the album spinner
		ArrayAdapter<String> albumAdapter 
		= new ArrayAdapter<String>(controller,
				android.R.layout.simple_spinner_dropdown_item,
				albums);
		albumSpinner.setAdapter(albumAdapter);
		albumSpinner.setLayoutParams(spinnerParams);
		albumSpinner.setOnItemSelectedListener(new AlbumSpinnerListener());
		// Create the song spinner
		ArrayAdapter<String> songAdapter 
		= new ArrayAdapter<String>(controller,
				android.R.layout.simple_spinner_dropdown_item,
				songs);
		songSpinner.setAdapter(songAdapter);
		songSpinner.setLayoutParams(spinnerParams);
		songSpinner.setOnItemSelectedListener(new SongSpinnerListener());
	}
	
	public void drawSpinners() {
		// Create the actual table rows
		TableRow artistRow = new TableRow(controller);
		TableRow albumRow = new TableRow(controller);
		TableRow songRow = new TableRow(controller);
		// Create the artist text
		TextView artistText = new TextView(controller);
		artistText.setText("Artist:");
		artistText.setLayoutParams(textParams);
		// Create the album text
		TextView albumText = new TextView(controller);
		albumText.setText("Album:");
		albumText.setLayoutParams(textParams);
		// Create the song text
		TextView songText = new TextView(controller);
		songText.setText("Song:");
		songText.setLayoutParams(textParams);
		// add rows to the table and views to rows
		artistRow.addView(artistText);
		artistRow.addView(artistSpinner);
		albumRow.addView(albumText);
		albumRow.addView(albumSpinner);
		songRow.addView(songText);
		songRow.addView(songSpinner);
		table.addView(artistRow, startIndex);
		added.put("artist", artistRow);
		table.addView(albumRow, startIndex + 1);
		added.put("album", albumRow);
		table.addView(songRow, startIndex + 2);
		added.put("song", songRow);
	}
	
	private int repopulate() {
		int index = 0;
		switch (mostSignificant) {
		case DEFAULT_TYPE:
				initLibrary();
				break;
		case ARTIST_TYPE:
				index = repopulateByArtist();
				break;
		case ALBUM_TYPE:
				repopulateByAlbum();
				break;
		case SONG_TYPE:
				repopulateBySong();
				break;
		}
		return index;
	}
	
	private int repopulateByArtist() {
		// Clear out the album and song lists before repopulating
		System.out.println("Repopulating by artist");
		albums = new ArrayList<String>();
		songs = new ArrayList<String>();
		albums.add(ALBUM_DEFAULT); songs.add(SONG_DEFAULT);
		try { // Repopulate the lists
			String currentAlbum = "";
			JSONObject albumJSON = library.getJSONObject(selected[ARTIST_TYPE]);
			Iterator<String> albumIterator = albumJSON.keys();
			while (albumIterator.hasNext()) { // For each album by the artist
				currentAlbum = albumIterator.next();
				albums.add(currentAlbum);
				JSONArray songList = albumJSON.getJSONArray(currentAlbum);
				for (int i = 0; i < songList.length(); i++) { // For each song in the album
					songs.add(songList.getString(i));
				}
			}
		} catch (JSONException e) {
			// TODO error handling
		}
		return artists.indexOf(selected[ARTIST_TYPE]);
	}
	
	private void repopulateByAlbum() {
		System.out.println("Repopulating by album");
		// Clear out the spinner lists before repopulating
		artists = new ArrayList<String>();
		albums = new ArrayList<String>();
		songs = new ArrayList<String>();
		artists.add(ARTIST_DEFAULT); songs.add(SONG_DEFAULT);
		try { // Repopulate the lists
			String currentArtist = "";
			String currentAlbum = "";
			Iterator<String> artistIterator = library.keys();
			while (artistIterator.hasNext()) { // Find the writing artist
				currentArtist = artistIterator.next();
				JSONObject artistJSON = library.getJSONObject(currentArtist);
				Iterator<String> albumIterator = artistJSON.keys();
				ArrayList<String> albumsByArtist = new ArrayList<String>(); // Temp array in case its the right artist
				albumsByArtist.add(ALBUM_DEFAULT);
				while (albumIterator.hasNext()) { // For each album by the artist
					currentAlbum = albumIterator.next();
					albumsByArtist.add(currentAlbum);
					// Don't bother searching the songs if this isn't the correct album
					if (!currentAlbum.equals(selected[ALBUM_TYPE])) continue;
					JSONArray songList = artistJSON.getJSONArray(currentAlbum);
					for (int i = 0; i < songList.length(); i++) { // For each song on the album
						songs.add(songList.getString(i));
					}
				}
				if (currentAlbum.equals(selected[ALBUM_TYPE])) { // We got it, add the info and get out
					albums = albumsByArtist;
					artists.add(currentArtist);
					break;
				}
			}
		} catch (JSONException e) {
			// TODO error handling
		}
	}
	
	private void repopulateBySong() {
		System.out.println("Repopulating by song");
		// Clear out the spinner lists before repopulating
		artists = new ArrayList<String>();
		albums = new ArrayList<String>();
		songs = new ArrayList<String>();
		artists.add(ARTIST_DEFAULT); albums.add(ALBUM_DEFAULT);
		boolean found = false; // toggle when correct song is found
		try { // Repopulate the lists
			String currentArtist = "";
			String currentAlbum = "";
			String currentSong = "";
			Iterator<String> artistIterator = library.keys();
			while (artistIterator.hasNext()) { // Find the writing artist
				currentArtist = artistIterator.next();
				JSONObject artistJSON = library.getJSONObject(currentArtist);
				Iterator<String> albumIterator = artistJSON.keys();
				while (albumIterator.hasNext()) {
					currentAlbum = albumIterator.next();
					ArrayList<String> songsByArtist = new ArrayList<String>(); // Temp array in case its the right song
					songsByArtist.add(SONG_DEFAULT);
					JSONArray songList = artistJSON.getJSONArray(currentAlbum);
					for (int i = 0; i < songList.length(); i++) {
						currentSong = songList.getString(i);
						songsByArtist.add(currentSong);
						if (currentSong.equals(selected[SONG_TYPE])) found = true;
					}
					if (found) { // We got it, set the list values and clean up
						artists.add(currentArtist);
						albums.add(currentAlbum);
						songs = songsByArtist;
						break;
					}
				}
			}
		} catch (JSONException e) {
			// TODO error handling
		}
	}
	
	private void redraw(int selectedArtist, int selectedAlbum, int selectedSong) {
		//System.out.println("Artist list: " + artists);
		//System.out.println("Album list: " + albums);
		//System.out.println("Song list: " + songs);
		// create the new array adapters
		ArrayAdapter<String> artistAdapter 
		= new ArrayAdapter<String>(controller,
				android.R.layout.simple_spinner_dropdown_item,
				artists);
		ArrayAdapter<String> albumAdapter 
		= new ArrayAdapter<String>(controller,
				android.R.layout.simple_spinner_dropdown_item,
				albums);
		ArrayAdapter<String> songAdapter 
		= new ArrayAdapter<String>(controller,
				android.R.layout.simple_spinner_dropdown_item,
				songs);
		// Set the adapters to each spinner
		artistSpinner.setAdapter(artistAdapter);
		artistSpinner.setSelection(selectedArtist);
		albumSpinner.setAdapter(albumAdapter);
		albumSpinner.setSelection(selectedAlbum);
		songSpinner.setAdapter(songAdapter);
		songSpinner.setSelection(selectedSong);
		// Let the spinners know they changed
		artistAdapter.notifyDataSetChanged();
		albumAdapter.notifyDataSetChanged();
		songAdapter.notifyDataSetChanged();
	}
	
	private void recalculateMostSignificant() {
		if (!selected[SONG_TYPE].equals(SONG_DEFAULT)) {
			mostSignificant = SONG_TYPE;
		} else if (!selected[ALBUM_TYPE].equals(ALBUM_DEFAULT)) {
			mostSignificant = ALBUM_TYPE;
		} else if (!selected[ARTIST_TYPE].equals(ARTIST_DEFAULT)) {
			mostSignificant = ARTIST_TYPE;
		} else {
			mostSignificant = DEFAULT_TYPE; // Nothing selected
		}
	}
	
	private class ArtistSpinnerListener implements OnItemSelectedListener {
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (mostSignificant > ARTIST_TYPE) return; // Leave it to the more significant spinners
			if (inProgress) return; // Don't redraw if we're already redrawing
			String spinnerText = parent.getItemAtPosition(position).toString();
			if (spinnerText.equals(selected[ARTIST_TYPE])) return; // Prevent infinite looping
			inProgress = true;
			System.out.println("Initiating lockdown");
			System.out.println("New artist selected! Old artist = " + selected[ARTIST_TYPE] + " new artist = " + spinnerText);
			mostSignificant = ARTIST_TYPE;
			if (spinnerText.equals(ARTIST_DEFAULT)) { // Nothing is selected anymore
				mostSignificant = 0;
				selected[ARTIST_TYPE] = ARTIST_DEFAULT;
			} else { // Recalculate the album and song lists for the selected artist
				selected[ARTIST_TYPE] = spinnerText;
			}
			System.out.println("Artist calling repopulate");
			int index = repopulate();
			redraw(index, 0, 0);
			inProgress = false;
			System.out.println("Exiting lockdown");
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// TODO I don't know what yet, but something here.
		}
	}
	
	private class AlbumSpinnerListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (mostSignificant > ALBUM_TYPE) return; // Leave it to the more significant spinners
			if (inProgress) return; // Don't redraw if we're already redrawing
			String spinnerText = parent.getItemAtPosition(position).toString();
			if (spinnerText.equals(selected[ARTIST_TYPE])) return; // Prevent infinite looping
			mostSignificant = ALBUM_TYPE;
			if (spinnerText.equals(ALBUM_DEFAULT)) {
				recalculateMostSignificant();
				selected[ALBUM_TYPE] = ALBUM_DEFAULT;
			} else {
				selected[ALBUM_TYPE] = spinnerText;
			}
			System.out.println("Album calling repopulate");
			repopulate();
			redraw(0, 0, 0);
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// TODO I don't know what yet, but something here.
		}
	}
	
	private class SongSpinnerListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (inProgress) return; // Don't redraw if we're already redrawing
			String spinnerText = parent.getItemAtPosition(position).toString();
			if (spinnerText.equals(selected[SONG_TYPE])) return; // Prevent infinite looping
			mostSignificant = SONG_TYPE; // Song is ALWAYS the most significant if selected
			if (spinnerText.equals(SONG_DEFAULT)) {
				recalculateMostSignificant();
				selected[SONG_TYPE] = SONG_DEFAULT;
			} else {
				selected[SONG_TYPE] = spinnerText;
			}
			System.out.println("Song calling repopulate");
			repopulate();
			redraw(0, 0, 0);
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
			// TODO I don't know what yet, but something here.
		}
	}
}
