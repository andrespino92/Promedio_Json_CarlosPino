package com.uninorte.promediocalculador;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks 
{

	private NavigationDrawerFragment mNavigationDrawerFragment;

	private CharSequence mTitle;
	
	private SQLiteDatabase myDB = null;
	private final String BD_NOMBRE = "BaseDatosPrueba";
    private final String BD_TABLA  = "materias";
    private final String BD_TABLA_INFO  = "infomateria";
    public String materia;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) 
	{
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment;
		switch (position)
		{
			case 0:
				fragmentManager.beginTransaction().replace(R.id.container,PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			case 1:
				fragment = new materia();
				fragmentManager.beginTransaction().replace(R.id.container,PlaceholderFragment.newInstance(position+1)).commit();
				fragmentManager.beginTransaction().replace(R.id.container,fragment,"materia").commit();
				break;
			case 2:
				fragment = new promedio();
				fragmentManager.beginTransaction().replace(R.id.container,PlaceholderFragment.newInstance(position+1)).commit();
				fragmentManager.beginTransaction().replace(R.id.container,fragment,"promedio").commit();				
				break;
	
			default:
				break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.inicio);
			break;
		case 2:
			mTitle = getString(R.string.materias);
			break;
		case 3:
			mTitle = getString(R.string.promedio);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			//Inicializamos la base
	        SQLiteDatabase myDB = null;
	        myDB = getBaseContext().openOrCreateDatabase(BD_NOMBRE, 1, null);	        
	        myDB.execSQL("DROP TABLE IF EXISTS "+BD_TABLA);
	        myDB.execSQL("DROP TABLE IF EXISTS "+BD_TABLA_INFO);
	        reloadFragment("materia");
			Toast.makeText(this, "Restaurado Todo", Toast.LENGTH_LONG).show();
			return true;
		}
		if(id==R.id.action_materia)
		{
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("¿Ingrese Materia a Eliminar?");
    		final EditText input = new EditText(this);
    		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
    		builder.setView(input);
    		builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() 
    		{ 
    		    @Override
    		    public void onClick(DialogInterface dialog, int which) 
    		    {
    		    	materia = input.getText().toString();
    		    	eliminarmateria(materia);    		    	    				
    		    }
    		});
    		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
    		{
    		    @Override
    		    public void onClick(DialogInterface dialog, int which) 
    		    {
    		        dialog.cancel();
    		    }
    		});
    		builder.show();			
		}
		return super.onOptionsItemSelected(item);
	}
		
	public static class PlaceholderFragment extends Fragment 
	{
		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
	
	public void eliminarmateria(String mat)
	{
		//Inicializamos la base
        myDB = null;
        //creo la base de datos        
        myDB = getBaseContext().openOrCreateDatabase(BD_NOMBRE, 1, null);        
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA + " (materia VARCHAR, creditos INTEGER);");
      //Comprobamos que la materia no exista en la base de datos
        String[] campos = new String[] {"materia,creditos"};    
        String[] args = {mat};
        Cursor c = myDB.query(BD_TABLA, campos,"materia=?", args, null, null, null);
        if(c==null || c.getCount()==0)
        {
        	Toast.makeText(this, "No Existe Materia", Toast.LENGTH_LONG).show();
        }
        else
        {        
        	myDB.delete(BD_TABLA, "materia='"+mat+"'", null);
        	reloadFragment("materia");
        	Toast.makeText(this, "Materia Eliminada", Toast.LENGTH_LONG).show();
        }
	}	

	public void reloadFragment(String tag)
	{
	   	// Reload current fragment
    	Fragment frg = null;
    	frg = getSupportFragmentManager().findFragmentByTag(tag);
    	final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	ft.detach(frg);
    	ft.attach(frg);
    	ft.commit();
	}
}
