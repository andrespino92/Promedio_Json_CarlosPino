package com.uninorte.promediocalculador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class pantalla_inicio extends DialogFragment
{
	 private final String BD_NOMBRE = "BaseDatosPrueba";
     private final String BD_TABLA_PROMEDIO = "promedio";
     private final String BD_TABLA_INFO  = "infomateria";
     private final String BD_TABLA  = "materias";
	 private String ca="",pa="",cc="";
	 private View vi;
	 Toast toast;
	 ProgressDialog dialog = null;
	 
	 
	 
	 @Override   
	 public void onCreate(Bundle savedInstanceState) 
	 {       
		 super.onCreate(savedInstanceState);    
		 setCancelable(false);
		 
		 
	 }
	
	 @Override  
	 public Dialog onCreateDialog(Bundle savedInstanceState) 
	 { 
		 vi = getActivity().getLayoutInflater().inflate(R.layout.pantalla_inicio, null);
		 	     
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		 builder.setView(vi);
		 builder.setTitle(R.string.bv);
		 builder.setPositiveButton(android.R.string.ok, 
		 new DialogInterface.OnClickListener() 
		 {
				 	@Override
		            public void onClick(DialogInterface dialog, int which)
		            {
		                //Do nothing here solo para compatibilidad
		            }
		         });
		 return builder.create();		
	}  
	@SuppressLint("ShowToast")
	@Override
	 public void onStart()
	 {
		//Sobre escribimos el listener para que se realicen oopiones reuqeridas
	     super.onStart();    //super.onStart() is where dialog.show() is actually called 
	     AlertDialog d = (AlertDialog)getDialog();
	     	     
	     if (isNetworkAvailable()) 
			{	   
	    	 
	       		CargarDatos cd = new CargarDatos();
				cd.execute("http://augustodesarrollador.com/promedio_app/read.php");
			}
	     toast = Toast.makeText(getActivity().getApplicationContext(),"Datos agregados Correctamente",Toast.LENGTH_SHORT);
	     if(d != null)
	     {
	         Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	         positiveButton.setOnClickListener(
	        		 new View.OnClickListener() 
	        		 {
	                     @Override
	                     public void onClick(View v)
	                     {
	                    	 //Controlar si se debe salir del dialog
	                    	 if(!InsertarPromedio(vi))
	                    	 {    
	                    		 
	                    	 }
	                    	 else
	                    	 {
	                    		//Inicializamos la base
	             		        SQLiteDatabase myDB = null;
	             		        //creo la base de datoss		        
	             		        myDB = getActivity().getBaseContext().openOrCreateDatabase(BD_NOMBRE, 1, null);		        
	             		        //ahora creo una tabla
	             		        myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_PROMEDIO + " (ca DECIMAL, pa DECIMAL,cc DECIMAL);");
	             		        Cursor c = myDB.query(BD_TABLA_PROMEDIO, null,null,null, null, null, null);
	             		        
	             		        if(c==null || c.getCount()==0)
	             		        { 
	             		        	myDB.execSQL("INSERT INTO "+ BD_TABLA_PROMEDIO + " (ca,pa,cc)"+ " VALUES ('"+ca+"','"+pa+"','"+cc+"');");
	            		        }
	            		        else
	            		        {
	            		        	Toast.makeText(getActivity().getBaseContext(), "Ya existe", Toast.LENGTH_LONG).show();
	            		        }	
	                    		 
		                         toast.setText("Datos Ingresados Correctamente");
		                       	 toast.show();
		                       	 dismiss();		                       	 
		                       	 //recargar layout menuprincipal		                       
		                       	((MainActivity)getActivity()).reloadFragment("inicio");
		                       
	                        }
	                     }
	                 });
	     }
	 }
		private boolean InsertarPromedio(View v){
			 //La primera vez que el usuario ingresa, se debe verificar, que el promedio deseado
			 boolean go = true;
			 
		        	EditText txt = (EditText)v.findViewById(R.id.txt_creditos_aprobados);
				   	 String dato_cred_cursados = txt.getText().toString();
				   	 if(dato_cred_cursados.equals(""))
				   	 {
				   		txt.setError("Olvidaste colocar los creditos aprobados!");
				   		 go =false;
				   	 }else
				   	 {
				   		 if(Integer.parseInt(dato_cred_cursados)>0)
				   		 {
				   			ca = dato_cred_cursados;
				   		 }
				   		 else
				   		 {
				   			txt.setError("Los creditos son incorrecto!");
					   		 go =false;
				   		 }
				   	 }
				   	 
				   	 txt = (EditText)v.findViewById(R.id.txt_promedio_acumulado);
				   	 String promAcum = txt.getText().toString();
				   	 if(promAcum.equals(""))
				   	 {
				   		txt.setError("Olvidaste colocar el promedio acumulado!");
				   		 go =false;
				   	 }
				   	 else
				   	 {
				   		 if(Double.parseDouble(promAcum)>0.0 && Double.parseDouble(promAcum)<=5.0)
				   		 {
				   			 pa = promAcum;
				   		 }
				   		 else
				   		 {
				   			txt.setError("El promedio acumulado es incorrectro (0 a 5)!");
					   		 go =false;
				   		 }
				   	 }				     
				   	
		   	 
			return go;
		}
		
		//OTOR
		public class CargarDatos extends AsyncTask<String, Void, String>
		{
	        /*Atributos para petición http*/
	        private HttpClient httpclient;
	        private HttpPost httppost;
	        private HttpResponse response;
	        private JSONObject obj=null,obj1=null;
			@Override
			protected String doInBackground(String... parameters) 
			{
				StringBuilder answer = new StringBuilder();
				try
				{
					InputStream is;
					String line ="";
					BufferedReader rd;
						httpclient = new DefaultHttpClient();
		                httppost = new HttpPost(parameters[0]);
		                response = httpclient.execute(httppost);
		                is = response.getEntity().getContent();		                
		                rd = new BufferedReader(new InputStreamReader(is));
		                while((line = rd.readLine())!=null){
		                    answer.append(line);
		                }
	            }catch (ClientProtocolException ex){
	                ex.printStackTrace();
	            }catch (IOException ex){
	                ex.printStackTrace();
	            }
				//Log.d("Respuesta",answer.toString());
				return answer.toString();
				
			}
			@Override
			protected void onPostExecute(String data)
			{					
				 try
				 {
					//Inicializamos la base
				    SQLiteDatabase myDB = null;
				    //creo la base de datos
				    myDB =vi.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
				    //ahora creo una tabla
				    myDB.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA_INFO + " (materia VARCHAR, evaluacion VARCHAR,porcentaje VARCHAR,nota DECIMAL);");
				
				  //Inicializamos la base
				    SQLiteDatabase myDB1 = null;
			        //creo la base de datos
			        myDB1 = vi.getContext().openOrCreateDatabase(BD_NOMBRE, 1, null);
			        //ahora creo una tabla
			        myDB1.execSQL("CREATE TABLE IF NOT EXISTS "+ BD_TABLA + " (materia VARCHAR, creditos INTEGER);");
				    
					 
					String status="";
							
					obj = new JSONObject(data);
			        status = obj.getString("status");
			        JSONArray materias = obj.getJSONArray("materias");			                
			        
				    if(status.equals("ok"))
				    {				            	 
				    	int sizemateria = materias.length();
				        Log.d("Tamaño",sizemateria+"");
				        for (int i=0;i<sizemateria;i++) 
				        {					 
				        	JSONObject mat = materias.getJSONObject(i);
						    String nom_mate = mat.getString("nombre_materia");							    
						             						            
						    JSONArray componentes = mat.getJSONArray("componetes");	
						    
						    //Log.d("Materia: ",nom_mate);
						    String a="";
				            for (int j=0;j<componentes.length();j++) 
					        {
				            	JSONObject comp = componentes.getJSONObject(j);
				                String eva = comp.getString("desc");
				                		 
				                String peso = comp.getString("peso");			                
				                
				                a = a + eva+"="+peso+" __ ";
				                //Log.d("Todo: ",eva+"= "+peso1);
				                double nota=0.0;
				                
				                myDB.execSQL("INSERT INTO "+ BD_TABLA_INFO + " (materia,evaluacion,porcentaje,nota)"+ " VALUES ('"+nom_mate+"','"+eva+"','"+peso+"','"+nota+"');");
					        }
				            int credi=0;
				            myDB.execSQL("INSERT INTO "+ BD_TABLA + " (materia,creditos)"+ " VALUES ('"+nom_mate+"','"+credi+"');");
				            //Log.d("Materia: ",i+": "+nom_mate+": "+a);
				            a="";
				        }
				        dialog.dismiss();
				    }
				    else
				    {			            	 
				    	Toast.makeText(vi.getContext(),"No hay historial", Toast.LENGTH_LONG).show();
				    }
					 
				 }
				 catch (JSONException ex)
				 {
					 ex.printStackTrace();
				 }
			}
		}
							
					
		//NUEVO
		private boolean isNetworkAvailable() 
		{
			ConnectivityManager manager = (ConnectivityManager) vi.getContext().getSystemService(vi.getContext().CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			boolean isNetworkAvaible = false;
			if (networkInfo != null && networkInfo.isConnected()) 
			{
				isNetworkAvaible = true;
				dialog = ProgressDialog.show(vi.getContext(),"", "Cargando Materias...",true);
				//Toast.makeText(vi.getContext(), "Con acceso a internet", Toast.LENGTH_SHORT).show();
			} 
			else
			{
				Toast.makeText(vi.getContext(), "Sin acceso a internet", Toast.LENGTH_SHORT).show();
			}
			return isNetworkAvaible;
		}


		
}
