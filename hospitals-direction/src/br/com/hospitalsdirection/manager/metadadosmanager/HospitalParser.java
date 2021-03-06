package br.com.hospitalsdirection.manager.metadadosmanager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class HospitalParser {
	
	
	private SharedPreferences sharedPreferences;
	private HospitalsPublic hospitalsPublic;

	public   HospitalParser(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	
	public List<Hospital> parse(String resultado) {
		hospitalsPublic = new HospitalsPublic();
		List<Hospital> hospitals = new ArrayList<Hospital>();
		try {

			final JSONObject json = new JSONObject(resultado);
			String nextToken = null;
			if(json.has("next_page_token")){
				nextToken =json.getString("next_page_token");
			}
			final JSONArray jsonHospital = json.getJSONArray("results");
			int numberHospital = jsonHospital.length();

			for (int i = 0; i < numberHospital; i++) {
				Hospital hospital = new Hospital();
				final JSONObject hospitalObject = jsonHospital.getJSONObject(i);
				hospital.setNome(hospitalObject.getString("name"));
				final JSONObject locaction = hospitalObject.getJSONObject("geometry").getJSONObject("location");
				hospital.setLatitude(locaction.getDouble("lat"));
				hospital.setLongitude(locaction.getDouble("lng"));
				if(sharedPreferences.getBoolean("public", false)){
				 if(containsString(hospital.getNome())){
					 hospitals.add(hospital);
				 }
				}else{
					 hospitals.add(hospital);
				}
				
				hospital.setNextToken(nextToken);
			}


		} catch (JSONException e) {
			Log.e(e.getMessage(), "Google JSON Parser - ");
		}
		return hospitals;
	}


	public boolean containsString(String hospital){
		boolean contain = false;
		List<String> hospitalsPublic = this.hospitalsPublic.getHospitaisPublic();
		for (String string : hospitalsPublic) {
			if(string.equalsIgnoreCase(hospital)){
				contain=true;
				break;
			}
		}
		return contain;
	}
}
