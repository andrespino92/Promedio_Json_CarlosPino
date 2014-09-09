package com.uninorte.promediocalculador;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;


public class info_materia extends Fragment
{		
	public View rootView;
	public int creditos;
	TextView txtmateria,txtcreditos;
	private TabHost tabcontent;
	private TabSpec pestaña;
	private final String BD_NOMBRE = "BaseDatosPrueba";
    private final String BD_TABLA  = "materias";
    private final String BD_TABLA_INFO  = "infomateria";
    private SQLiteDatabase myDB = null;
    public String id_materia;
    Button addevaluacion,addnota,btnlogrocalcular;
    EditText txtevaluacion,txtpeso,txtnota,txtlogronota,txtpromediototal;
    TextView txtporce,totalnota;
    public String evalua;
    public int peso;
    ArrayList<String> evaluaciones = new ArrayList<String>();
    Spinner eval_notas;
    ArrayAdapter<String> adap;
    
    private TableLayout tabla;
	private TableLayout cabecera;
	private TableRow.LayoutParams layFila;
	private TableRow.LayoutParams layEvaluacion;
	private TableRow.LayoutParams layPeso;
	private TableRow.LayoutParams layNota;
	
	private Resources rs;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.info_materia, container, false);
		
		txtmateria = (TextView)rootView.findViewById(R.id.txtmateria);
		txtmateria.setText(getArguments().getString("mat"));
		
		txtcreditos = (TextView)rootView.findViewById(R.id.txtcreditos);
		
		tabcontent = (TabHost) rootView.findViewById(android.R.id.tabhost);
		tabcontent.setup();
		
		pestaña = tabcontent.newTabSpec("Cortes");
		pestaña.setContent(R.id.evaluaciones);
		pestaña.setIndicator("Cortes",getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
		tabcontent.addTab(pestaña);
		
		pestaña = tabcontent.newTabSpec("Nueva Nota");
		pestaña.setContent(R.id.addnotas);
		pestaña.setIndicator("Nueva Nota",getResources().getDrawable(android.R.drawable.ic_delete));
		tabcontent.addTab(pestaña);
		
		pestaña = tabcontent.newTabSpec("Logros");
		pestaña.setContent(R.id.logro);
		pestaña.setIndicator("Logro",getResources().getDrawable(android.R.drawable.ic_delete));
		tabcontent.addTab(pestaña);
		
		pestaña = tabcontent.newTabSpec("Notas");
		pestaña.setContent(R.id.notas);
		pestaña.setIndicator("Notas",getResources().getDrawable(android.R.drawable.ic_delete));
		tabcontent.addTab(pestaña);	
		
		
		id_materia = getArguments().getString("mat");
		VerificarMateria(id_materia);
		
		txtevaluacion = (EditText)rootView.findViewById(R.id.txtevaluacion);
		txtpeso = (EditText)rootView.findViewById(R.id.txtpeso);
		txtporce = (TextView)rootView.findViewById(R.id.txtporcen);
		txtporce.setText(ActualizarPorcentaje()+"");
		
		eval_notas = (Spinner)rootView.findViewById(R.id.eva_notas);
		
		rs = rootView.getResources();
		tabla = (TableLayout)rootView.findViewById(R.id.tabla);
		cabecera = (TableLayout)rootView.findViewById(R.id.cabecera);
		layFila = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1);
		layEvaluacion = new TableRow.LayoutParams(200,100,1);
		layPeso = new TableRow.LayoutParams(200,100,1);
		layNota= new TableRow.LayoutParams(200,100,1);
		
		ActualizarNotas();
		AgregarCabecera();
		
		
		adap = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item,evaluaciones);
		adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eval_notas.setAdapter(adap);
		
		addevaluacion = (Button)rootView.findViewById(R.id.addevaluacion);
		addevaluacion.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{			
				if(!txtevaluacion.getText().toString().equals("") && !txtpeso.getText().toString().equals(""))
				{
					evalua = txtevaluacion.getText().toString();
					peso = Integer.parseInt(txtpeso.getText().toString());
					Double nota=0.0;
					Boolean ver=false;
					
					//Inicializamos la base
			        SQLiteDatabase myDB = null;
			        //creo la base de datos
			        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
			        //ahora creo una tabla
			        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
			        String[] campos = new String[] {"materia,evaluacion,porcentaje"};
			        String[] args = {getArguments().getString("mat")};
			        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
			        if(c==null || c.getCount()==0)
			        {
			            myDB.execSQL("INSERT INTO "+ BD_TABLA_INFO + " (materia,evaluacion,porcentaje,nota)"+ " VALUES ('"+id_materia+"','"+evalua+"','"+peso+"','"+nota+"');");
			            txtevaluacion.setText("");
		        		txtpeso.setText("");
		        		txtporce.setText(ActualizarPorcentaje()+"");
		        		ActualizarNotas();
			            Toast.makeText(rootView.getContext(), "Evaluación Agregada", Toast.LENGTH_SHORT).show();
			        }
			        else
			        {			   
			        	while(c.moveToNext() && ver==false)
			            {
			        		int col_eva = c.getColumnIndexOrThrow("evaluacion");
			        		
			        		String eva_dato = c.getString(col_eva);
			        		if(eva_dato.toString().equals(evalua))
			        		{
			        			ver=true;
			        		}
			            }
			        	
			        	if(ver==false)
			        	{			        	
				        	if(ActualizarPorcentaje()+peso<=100 )
				        	{
				        		
				        		myDB.execSQL("INSERT INTO "+ BD_TABLA_INFO + " (materia,evaluacion,porcentaje,nota)"+ " VALUES ('"+id_materia+"','"+evalua+"','"+peso+"','"+nota+"');");
				        		txtevaluacion.setText("");
				        		txtpeso.setText("");
				        		txtporce.setText(ActualizarPorcentaje()+"");
				        		ActualizarNotas();
					            Toast.makeText(rootView.getContext(), "Evaluación Agregada", Toast.LENGTH_LONG).show();
				        	}
				        	else
				        	{
				        		txtpeso.setText("");
				        		Toast.makeText(rootView.getContext(), "Porcentaje mal Digitado", Toast.LENGTH_LONG).show();
				        	}
			        	}
			        	else
			        	{
			        		txtevaluacion.setText("");
			        		Toast.makeText(rootView.getContext(), "Evaluacion ya ingresada", Toast.LENGTH_LONG).show();
			        	}
			        }
				}
				else
				{
					Toast.makeText(rootView.getContext(), "Llene todos los campos", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		txtnota = (EditText)rootView.findViewById(R.id.txtnota);
		
		eval_notas.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id)
			{
				//Toast.makeText(rootView.getContext(), "Ha pulsado el item " + eval_notas.getSelectedItem(), Toast.LENGTH_SHORT).show();
				//Inicializamos la base
		        SQLiteDatabase myDB = null;
		        //creo la base de datos
		        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
		        //ahora creo una tabla
		        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
		        String[] campos = new String[] {"materia,evaluacion,nota"};
		        String[] args = {getArguments().getString("mat")};
		        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
		        
		        if(c==null || c.getCount()==0)
		        {
		        }
		        else
		        {
		        	while(c.moveToNext())
		        	{		        		
		        		int col_mat = c.getColumnIndexOrThrow("materia");
				        String mat_dato = c.getString(col_mat);
				        /*int col_eval = c.getColumnIndexOrThrow("evaluacion");        		
			        	String eval_dato = c.getString(col_eval);
			        	int col_nota = c.getColumnIndexOrThrow("nota");        		
			        	Double nota_dato = c.getDouble(col_nota);
				        Toast.makeText(rootView.getContext(), "Mat: "+mat_dato+ " - eva: "+eval_dato+" - no: "+nota_dato, Toast.LENGTH_LONG).show();
				        */if(mat_dato.toString().equals(id_materia))
				        {
				        	int col_eval = c.getColumnIndexOrThrow("evaluacion");        		
				        	String eval_dato = c.getString(col_eval);
				        	int col_nota = c.getColumnIndexOrThrow("nota");        		
				        	Double nota_dato = c.getDouble(col_nota);
				        	
				        	if(eval_dato.toString().equals(eval_notas.getSelectedItem()))  
				        	{
				        		txtnota.setText("");
				        		if(nota_dato!=0.0)
				        		{
				        			txtnota.setText(nota_dato+"");
					        		myDB.close();
				        		}				        		
				        	}		        			
				        }
		        	}
		        }				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        });

		addnota = (Button)rootView.findViewById(R.id.addnota);
		addnota.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{			
				if(!txtnota.getText().toString().equals(""))
				{
					if(Double.parseDouble(txtnota.getText().toString())<=5.0)
					{
						double nota1=0.0;
						//Inicializamos la base
				        SQLiteDatabase myDB = null;
				        //creo la base de datos
				        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
				        //ahora creo una tabla
				        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
				        String[] campos = new String[] {"materia,evaluacion,nota"};
				        String[] args = {id_materia};
				        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
				        if(c==null || c.getCount()==0)
				        {              
				        }
				        else
				        {
					        while(c.moveToNext())
					        {		
							  	double nota = Double.parseDouble(txtnota.getText().toString());
							   	int col_mat = c.getColumnIndexOrThrow("materia");
							   	String mat_dato = c.getString(col_mat);
							   	int col_eval = c.getColumnIndexOrThrow("evaluacion");        		
					        	String eval_dato = c.getString(col_eval);
							   	if(mat_dato.toString().equals(id_materia) && eval_dato.toString().equals(eval_notas.getSelectedItem()))
							   	{
							   		ContentValues valores = new ContentValues();
							   		valores.put("nota",nota);	        		   	        		  
					        	    myDB.update(BD_TABLA_INFO, valores, "materia='"+id_materia+"' AND evaluacion='"+eval_notas.getSelectedItem()+"'", null);
					        	    Toast.makeText(rootView.getContext(), "Nota Asignada a la Evaluación", Toast.LENGTH_SHORT).show();
					        	    myDB.close();
							   	}			        	
					        }			       				      
				        }
					}				
					else
					{
						Toast.makeText(rootView.getContext(), "Nota mal digitada", Toast.LENGTH_LONG).show();
						txtnota.setText("");
					}
				}
				else
				{
					Toast.makeText(rootView.getContext(), "Digite nota de esta evaluación", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		totalnota = (TextView)rootView.findViewById(R.id.totalnota);
		
		tabcontent.setOnTabChangedListener(new OnTabChangeListener() 
		{
		    @Override
		    public void onTabChanged(String tabId) 
		    {		    	
		    	if(tabId.equals("Notas"))
		    	{		  
		    		//AgregarCabecera();
		    		tabla.removeAllViews();
		    		cabecera.removeAllViews();
		    		AgregarFilasTabla();
		    		AgregarCabecera();
		    	}
		    }
		});
		
		txtlogronota = (EditText)rootView.findViewById(R.id.txtlogronota);
		txtpromediototal = (EditText)rootView.findViewById(R.id.txtpromediototal);
		btnlogrocalcular = (Button)rootView.findViewById(R.id.btnlogrocalcular);
		btnlogrocalcular.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{			
				if(!txtlogronota.getText().toString().equals(""))
				{
					double suma = 0.0,sumatotal=0.0,resul=0.0,resultotal=0.0;
					double promdeseado = Double.parseDouble(txtlogronota.getText().toString());
					//Inicializamos la base
			        SQLiteDatabase myDB = null;
			        //creo la base de datos
			        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
			        //ahora creo una tabla
			        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
			        String[] campos = new String[] {"materia,porcentaje,nota"};
			        String[] args = {id_materia};
			        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
			        while(c.moveToNext())
			        {	
					   	int col_peso = c.getColumnIndexOrThrow("porcentaje");
					   	double peso_dato = (double)c.getInt(col_peso);
					   	peso_dato = peso_dato/(double)100;
					   	
					   	int col_nota = c.getColumnIndexOrThrow("nota");        		
			        	double nota_dato = c.getDouble(col_nota);
			        	
			        	if(nota_dato!=0.0)
			        	{
			        		resul = resul+(peso_dato*nota_dato);
			        		suma = suma+peso_dato;
			        	}
			        	else
			        	{
			        		sumatotal = sumatotal+peso_dato;
			        	}			        						   			       
			        }
			        resultotal = (promdeseado-resul)/(sumatotal);
			        txtpromediototal.setText("Necesitas sacar "+resultotal+" en los cortes restantes.");			        
				}
				else
				{
					Toast.makeText(rootView.getContext(), "Digite Promedio Deseado", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		return rootView;
	}
	
	public void AgregarFilasTabla()
	{
		TableRow fila;
		TextView txtevaluacion;
		TextView txtpeso;
		TextView txtnota;
		double suma=0.0;
		
		//Inicializamos la base
        SQLiteDatabase myDB = null;
        //creo la base de datos
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
        String[] campos = new String[] {"evaluacion,porcentaje,nota"};
        String[] args = {id_materia};
        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
        
        while(c.moveToNext())
        {
        	
        	int col_eva = c.getColumnIndexOrThrow("evaluacion");
		   	String eva_dato = c.getString(col_eva);
		   	int col_peso = c.getColumnIndexOrThrow("porcentaje");        		
        	int peso_dato = c.getInt(col_peso);
        	int col_nota = c.getColumnIndexOrThrow("nota");
        	double nota_dato = c.getDouble(col_nota);        	        	
        	
			fila = new TableRow(rootView.getContext());
			fila.setLayoutParams(layFila);
			
			txtevaluacion = new TextView(rootView.getContext());
			txtpeso = new TextView(rootView.getContext());
			txtnota = new TextView(rootView.getContext());
			
			txtevaluacion.setText(eva_dato);
			txtevaluacion.setGravity(Gravity.CENTER_HORIZONTAL);
			txtevaluacion.setTextAppearance(rootView.getContext(),R.style.etiqueta);
			txtevaluacion.setBackgroundResource(R.drawable.tabla_celda);
			txtevaluacion.setLayoutParams(layEvaluacion);
			
			txtpeso.setText(String.valueOf(peso_dato));
			txtpeso.setGravity(Gravity.CENTER_HORIZONTAL);
			txtpeso.setTextAppearance(rootView.getContext(),R.style.etiqueta);
			txtpeso.setBackgroundResource(R.drawable.tabla_celda);
			txtpeso.setLayoutParams(layPeso);
			
			if(nota_dato==0.0)
        	{
				txtnota.setText("-");
				txtnota.setGravity(Gravity.CENTER_HORIZONTAL);
				txtnota.setTextAppearance(rootView.getContext(),R.style.etiqueta);
				txtnota.setBackgroundResource(R.drawable.tabla_celda);
				txtnota.setLayoutParams(layNota);
        	}
			else
			{
				double a = (double)peso_dato;
				double b = (double)100;				
				double d = a/b;			
				suma = suma + (d*nota_dato); 				
				txtnota.setText(String.valueOf(nota_dato));
				txtnota.setGravity(Gravity.CENTER_HORIZONTAL);
				txtnota.setTextAppearance(rootView.getContext(),R.style.etiqueta);
				txtnota.setBackgroundResource(R.drawable.tabla_celda);
				txtnota.setLayoutParams(layNota);
			}
			
			fila.addView(txtevaluacion);
			fila.addView(txtpeso);
			fila.addView(txtnota);

			tabla.addView(fila);			
        }
        totalnota.setText(String.valueOf(suma));
	}
	
	public void AgregarCabecera()
	{
		TableRow fila;
		TextView txtevaluacion;
		TextView txtpeso;
		TextView txtnota;
		
		fila = new TableRow(rootView.getContext());
		fila.setLayoutParams(layFila);
		
		txtevaluacion = new TextView(rootView.getContext());
		txtpeso = new TextView(rootView.getContext());
		txtnota = new TextView(rootView.getContext());
		
		txtevaluacion.setText(rs.getString(R.string.notaeva));
		txtevaluacion.setGravity(Gravity.CENTER_HORIZONTAL);
		txtevaluacion.setTextAppearance(rootView.getContext(),R.style.etiqueta);
		txtevaluacion.setBackgroundResource(R.drawable.tabla_celda_cabecera);
		txtevaluacion.setLayoutParams(layEvaluacion);
		
		txtpeso.setText(rs.getString(R.string.notapeso));
		txtpeso.setGravity(Gravity.CENTER_HORIZONTAL);
		txtpeso.setTextAppearance(rootView.getContext(),R.style.etiqueta);
		txtpeso.setBackgroundResource(R.drawable.tabla_celda_cabecera);
		txtpeso.setLayoutParams(layPeso);
		
		txtnota.setText(rs.getString(R.string.notanota));
		txtnota.setGravity(Gravity.CENTER_HORIZONTAL);
		txtnota.setTextAppearance(rootView.getContext(),R.style.etiqueta);
		txtnota.setBackgroundResource(R.drawable.tabla_celda_cabecera);
		txtnota.setLayoutParams(layNota);
		
		fila.addView(txtevaluacion);
		fila.addView(txtpeso);
		fila.addView(txtnota);
		
		cabecera.addView(fila);
	}
	
	public void ActualizarNotas()
	{
		//Inicializamos la base
        SQLiteDatabase myDB = null;
        //creo la base de datos
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
        String[] campos = new String[] {"materia,evaluacion"};
        String[] args = {getArguments().getString("mat")};
        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
        
        if(c==null || c.getCount()==0)
        {
        }
        else
        {        
        	adap = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item,evaluaciones);
    		adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		adap.clear();
        	while(c.moveToNext())
            {
        		int col_eval = c.getColumnIndexOrThrow("evaluacion");        		
        		String eval_dato = c.getString(col_eval);
        		evaluaciones.add(eval_dato);    
            }
        	
        }

	}
	
	public int ActualizarPorcentaje()
	{
		int suma=0;
		
		//Inicializamos la base
        SQLiteDatabase myDB = null;
        //creo la base de datos
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje INTEGER,nota DECIMAL);");
        String[] campos = new String[] {"materia,porcentaje"};
        String[] args = {getArguments().getString("mat")};
        Cursor c = myDB.query(BD_TABLA_INFO, campos, "materia=?", args, null, null, null);
        
        if(c==null || c.getCount()==0)
        {
        }
        else
        {
        	while(c.moveToNext())
            {
        		int col_porcen = c.getColumnIndexOrThrow("porcentaje");
        		
        		int porcen_dato = c.getInt(col_porcen);
        		suma = suma+porcen_dato;
            }
        }
        return suma;
	}
		
	public void VerificarMateria(String materia)
	{
		int columna2 = 0;
		final int col;
		//Inicializamos la base
        myDB = null;
        //creo la base de datos        
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA + " (materia VARCHAR, creditos INTEGER);");
      //Comprobamos que la materia no exista en la base de datos
        String[] campos = new String[] {"materia,creditos"};
        String[] args = {materia};
        final Cursor c = myDB.query(BD_TABLA, campos, "materia=?", args, null, null, null);
        
        if(c==null || c.getCount()==0)
        {              
        }
        else
        {
	        if(c.moveToFirst())
	        {
	        	final int columna = c.getColumnIndexOrThrow("materia");
	        	columna2 = c.getColumnIndexOrThrow("creditos");   
	        		
	        	if(c.getInt(columna2)==0)
	        	{
	        		//Toast.makeText(rootView.getContext(), "La Materia: "+c.getString(columna)+", creditos vacio", Toast.LENGTH_LONG).show();
	        		
	        		AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
	        		builder.setTitle("Creditos de la Materia");
	        		final EditText input = new EditText(rootView.getContext());
	        		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_NUMBER);
	        		builder.setView(input);
	        		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
	        		{ 
	        		    @Override
	        		    public void onClick(DialogInterface dialog, int which) 
	        		    {
	        		    	creditos = Integer.parseInt(input.getText().toString());
	        		    	ContentValues valores = new ContentValues();
	        		    	valores.put("creditos",creditos);	        		   	        		  
	        		    	myDB.update(BD_TABLA, valores, "materia='"+c.getString(columna)+"'", null);
	        		    	txtcreditos.setText((creditos+"")+" "+txtcreditos.getText());
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
	        	else
	        	{
	        		txtcreditos.setText(c.getString(columna2)+" "+txtcreditos.getText());
	        	} 
	        } 
        }
	}
}