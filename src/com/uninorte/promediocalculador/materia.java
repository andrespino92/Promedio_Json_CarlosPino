package com.uninorte.promediocalculador;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class materia extends Fragment
{	
	Button addmateria;
	public String texto;
	private final String BD_NOMBRE = "BaseDatosPrueba";
    private final String BD_TABLA  = "materias";
    private final String BD_TABLA_INFO  = "infomateria";
    public View rootView;
    ArrayList<String> materias = new ArrayList<String>();
    ListView listar;
    ArrayAdapter<String> adaptador;
    private info_materia f_info_materia;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.materia, container, false);
		texto="";
		adaptador = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, materias);
		listar = (ListView)rootView.findViewById(R.id.listamaterias);
		adaptador.clear();
		BasesDatosBuscar();
	
		adaptador = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, materias);
		listar.setAdapter(adaptador);
		
		addmateria = (Button)rootView.findViewById(R.id.addmateria);
		addmateria.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{				
				AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
				builder.setTitle("Agregar Materia");
				final EditText input = new EditText(rootView.getContext());
				input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
				builder.setView(input);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
				{ 
				    @Override
				    public void onClick(DialogInterface dialog, int which) 
				    {				        
				        if(!input.getText().toString().equals(""))
				        {
				        	texto = input.getText().toString();
				        	BaseDatosInsertar(texto);
				        }
				        else
				        {
				        	Toast.makeText(rootView.getContext(), "Campo de texto vacio", Toast.LENGTH_SHORT).show();
				        }
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				{
				    @Override
				    public void onClick(DialogInterface dialog, int which) 
				    {
				        dialog.cancel();
				    }
				});

				builder.show();
			}
		});
		
		f_info_materia = new info_materia();
		listar.setOnItemClickListener(new OnItemClickListener(){
			 
		    @Override
		    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) 
		    {
		        // TODO Auto-generated method stub
		        //Toast.makeText(rootView.getContext(), "Ha pulsado el item " + materias.get(position), Toast.LENGTH_SHORT).show();
		        Bundle args = new Bundle();
		        args.putString("mat", materias.get(position));
		        f_info_materia.setArguments(args);
		        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.container, f_info_materia,"info_materia").addToBackStack("f_info_materia").commit();
		 
		    }
		 
		});
		return rootView;
	}
	
	private void BaseDatosInsertar(String materia)
	{
		//Inicializamos la base
        SQLiteDatabase myDB = null;
        //creo la base de datos
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA + " (materia VARCHAR, creditos INTEGER);");
        //Comprobamos que la materia no exista en la base de datos
        String[] campos = new String[] {"materia"};
        String[] args = {materia};
        int credi = 0;
        Cursor c = myDB.query(BD_TABLA, campos, "materia=?", args, null, null, null);
        
        if(c==null || c.getCount()==0)
        {
            //Toast.makeText(getBaseContext(), "No hay nada en el cursor", Toast.LENGTH_LONG).show();
            myDB.execSQL("INSERT INTO "+ BD_TABLA + " (materia,creditos)"+ " VALUES ('"+materia+"','"+credi+"');");
            adaptador.clear();
            BasesDatosBuscar();
            Toast.makeText(rootView.getContext(), "Materia Creada", Toast.LENGTH_LONG).show();
        }
        else
        {
        	Toast.makeText(rootView.getContext(), "La Materia ya Existe", Toast.LENGTH_LONG).show();
        }		
	}
	
	private void BasesDatosBuscar()
	{		
		//Inicializamos la base
        SQLiteDatabase myDB = null;
        /* Abrimos la base de datos.
         * Si no existía previamente se creará automáticamente. */
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
        //Creo tabla de login si no existia, aunque es imposible que no este creada, evitaremos posibles fallos
        //myDB.execSQL("DROP TABLE IF EXISTS "+BD_TABLA);
        //myDB.execSQL("DROP TABLE IF EXISTS "+BD_TABLA_INFO);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA + " (materia VARCHAR, creditos INTEGER);");
        //conuslto y meto en un cursor los registros de la tabla user
        Cursor c = myDB.query(BD_TABLA, null, null, null, null, null, null);
        if(c==null || c.getCount()==0)
        {
        	//Toast.makeText(rootView.getContext(), "No hay Materias Registradas", Toast.LENGTH_SHORT).show();
        }
        else
        {
        	//Con todo esto cojo el nombre de usuario con el que se registro
            //startManagingCursor(c);
        	
            StringBuilder builder = new StringBuilder("Saved events: \n");       
            while(c.moveToNext())
            {
            	int columna = c.getColumnIndexOrThrow("materia");
            	materias.add(c.getString(columna));           
            }
        }
	}
	
}