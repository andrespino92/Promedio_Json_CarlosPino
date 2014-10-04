package com.uninorte.promediocalculador;
import java.text.DecimalFormat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class promedio extends Fragment
{	
	EditText txtpromediototal,txtcreditosaprobados,txtpromactual,txtcredcursados,txtpromdeseado;
	Button calcularpromedio;
	pantalla_inicio pantalla_ini;
	private final String BD_NOMBRE = "BaseDatosPrueba";
    private final String BD_TABLA_PROMEDIO = "promedio";
	View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.promedio, container, false);
		
		txtcreditosaprobados = (EditText)rootView.findViewById(R.id.txtcreditosaprobados);
		txtpromactual = (EditText)rootView.findViewById(R.id.txtpromactual);
		txtcredcursados = (EditText)rootView.findViewById(R.id.txtcredcursados);
		txtpromdeseado = (EditText)rootView.findViewById(R.id.txtpromdeseado);
		
		txtpromediototal = (EditText)rootView.findViewById(R.id.txtpromediototal);			
		
		//Inicializamos la base
        SQLiteDatabase myDB = null;
        //creo la base de datoss		        
        myDB = rootView.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);		        
        //ahora creo una tabla
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_PROMEDIO + " (ca DECIMAL, pa DECIMAL,cc DECIMAL);");
        Cursor c = myDB.query(BD_TABLA_PROMEDIO, null,null,null, null, null, null);
        if(c==null || c.getCount()==0)
        {
        }
        else
        {
        	//Con todo esto cojo el nombre de usuario con el que se registro
            //startManagingCursor(c);            
            while(c.moveToNext())
            {
            	int c_a = c.getColumnIndexOrThrow("ca");
    		   	Double ca = c.getDouble(c_a);  
    		   	txtcreditosaprobados.setText(ca+"");
    		   	
    		   	int p_a = c.getColumnIndexOrThrow("pa");
    		   	Double pa = c.getDouble(p_a);
    		   	txtpromactual.setText(pa+"");
    		   	
    		   	int c_c = c.getColumnIndexOrThrow("cc");
    		   	Double cc = c.getDouble(c_c);
    			txtcredcursados.setText(cc+"");
            }
        }			
		
		calcularpromedio = (Button)rootView.findViewById(R.id.calcularpromedio);
		calcularpromedio.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{			
				if(!txtpromdeseado.getText().toString().equals("") )
				{
					double resul=0.0;
					double c_aprobado = Double.parseDouble(txtcreditosaprobados.getText().toString());
					double p_actual = Double.parseDouble(txtpromactual.getText().toString());
					double c_cursados = Double.parseDouble(txtcredcursados.getText().toString());
					double p_deseado = Double.parseDouble(txtpromdeseado.getText().toString());
					
					resul = ((p_deseado*(c_aprobado+c_cursados))-(p_actual*c_aprobado))/(c_cursados);
					
					DecimalFormat df = new DecimalFormat("0.00");					
					
					txtpromediototal.setText(df.format(resul));
				}
				else
				{
					Toast.makeText(rootView.getContext(), "Llene Todos los Campos", Toast.LENGTH_LONG).show();
					txtpromdeseado.setError("�Cual es tu promedio deseado?");
				}
			}
		});
		
		return rootView;
	}
	
}