package com.uninorte.promediocalculador;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class promedio extends Fragment
{	
	WebView wv;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.promedio, container, false);

		
		return rootView;
	}
	
}