package com.uninorte.promediocalculador;
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
	EditText txtpromediototal,txtcreditoscursados,txtpromactual,txtcredcursados,txtpromdeseado;
	Button calcularpromedio;
	View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.promedio, container, false);
		
		txtcreditoscursados = (EditText)rootView.findViewById(R.id.txtcreditoscursados);
		txtpromactual = (EditText)rootView.findViewById(R.id.txtpromactual);
		txtcredcursados = (EditText)rootView.findViewById(R.id.txtcredcursados);
		txtpromdeseado = (EditText)rootView.findViewById(R.id.txtpromdeseado);
		
		txtpromediototal = (EditText)rootView.findViewById(R.id.txtpromediototal);			
		
		calcularpromedio = (Button)rootView.findViewById(R.id.calcularpromedio);
		calcularpromedio.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{			
				if(!txtcreditoscursados.getText().toString().equals("") &&
				   !txtpromactual.getText().toString().equals("") &&
				   !txtcredcursados.getText().toString().equals("") &&
				   !txtpromdeseado.getText().toString().equals("") )
				{
					double resul=0.0;
					double c_aprobado = Double.parseDouble(txtcreditoscursados.getText().toString());
					double p_actual = Double.parseDouble(txtpromactual.getText().toString());
					double c_cursados = Double.parseDouble(txtcredcursados.getText().toString());
					double p_deseado = Double.parseDouble(txtpromdeseado.getText().toString());
					
					resul = ((p_deseado*(c_aprobado+c_cursados))-(p_actual*c_aprobado))/(c_cursados);
					
					txtpromediototal.setText("Necesitas dejar el semestre en: "+resul);
				}
				else
				{
					Toast.makeText(rootView.getContext(), "Llene Todos los Campos", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		return rootView;
	}
	
}