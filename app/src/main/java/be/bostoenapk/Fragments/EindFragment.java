package be.bostoenapk.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.bostoenapk.Activities.LoginActivity;
import be.bostoenapk.Model.AntwoordOptie;
import be.bostoenapk.Model.Dossier;
import be.bostoenapk.Model.Plaats;
import be.bostoenapk.Model.VragenDossier;
import be.bostoenapk.R;

public class EindFragment extends Fragment {
    private View view;
    private OnFragmentInteractionListener mListener;
    private TextView oplossing;
    Button wijziging;
    Button verzending;
    Button kiesReeks;
    private final String PREFS_NAME = "COM.BOSTOEN.BE";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.eindscherm_layout, container, false);

        //elementen van scherm in code ophalen
        oplossing = (TextView) view.findViewById(R.id.txtResultaat);
        wijziging = (Button) view.findViewById(R.id.btnWijzigen);
        verzending = (Button) view.findViewById(R.id.btnVerzenden);
        kiesReeks = (Button) view.findViewById(R.id.btnKiesReeks);

        //laats beantwoorde vraag resetten
        mListener.setLastVraag(null);

        //stringbuilder om later oplossingtekst van huidig dossier op te halen
        final StringBuilder tekst = new StringBuilder();

        //Ophalen van gegevens laatst ingevuld Dossier
        Integer lastDossier = mListener.getLastDossier();
        if(lastDossier!=null)
        {
            Dossier huidig = mListener.getDossier(lastDossier);
            if(huidig!=null)
            {
                tekst.append(huidig.getNaam()+": \n");
            }
        }



        //controleren of een laatste dossier is
        if(mListener.getLastDossier()!=null) {
            //alle VragenDossier die bij het huidig dossier horen ophalen ( hierin worden de antwoorden van de gebruiker opgeslagen)
            ArrayList<VragenDossier> vragenDossiers = mListener.getVragenDossiers(mListener.getLastDossier());
            //String met oplossingen halen van het laatste Dossier
            if (vragenDossiers != null) {

                for (VragenDossier vragenDossier : vragenDossiers) {
                    //alle antwoorden ophalen die horen bij de beantwoorde vraag van het VragenDossier
                    ArrayList<AntwoordOptie> antwoordOpties = mListener.getAntwoorden(vragenDossier.getAntwoordOptie());
                    Log.d("Antwoordopties", new Integer(antwoordOpties.size()).toString());

                    for (AntwoordOptie antwoordOptie : antwoordOpties) {
                        Log.d("Antwoordoptie conditie", new Boolean(antwoordOptie.getAntwoordTekst().equals(vragenDossier.getAntwoordTekst())).toString());
                        //indien de opgeslagen tekst in VragenDossier gelijk is aan de tekst van de Antwoordoptie heeft de gebruiker deze AntwoordOptie gekozen
                        if (antwoordOptie.getAntwoordTekst().equals(vragenDossier.getAntwoordTekst())) {
                            //conroleren of er bij de geselecteerde AntwoordOptie een oplossing hoort
                            if (antwoordOptie.getOplossing() != null && !antwoordOptie.getOplossing().equals("")) {
                                //oplossingtekst toevoegen aan StringBuilder
                                tekst.append(antwoordOptie.getOplossing() + "\n");
                                Log.d("Oplossing", antwoordOptie.getOplossing());
                            }
                        }
                    }
                }
            }
        }




        Log.d("Oplossing", tekst.toString());

        //oplossingen van vorige dossiers en van het laatste dossier op het scherm weergeven
        oplossing.setText(mListener.getOplossing()+ "\n" + tekst.toString());

        //naar instelliingenscherm
        wijziging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToInstellingen(mListener.getLastDossier());
            }
        });

        //email verzenden
        verzending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //oplossing van laatste dossier toevoegen aan bestaande oplossingen

                //controleren of het laatste dossier oplossingen heeft
                if(!tekst.toString().equals("") && tekst.toString()!=null)
                {
                    mListener.setOplossing(tekst.toString());
                }
                if(mListener.getLastPlaats()!=null)
                {
                    sendEmail(mListener.getLastPlaats());

                    //elementen op null zetten om later nieuwe reeksen te kunnen invullen
                    mListener.setLastDossier(null);
                    mListener.setLastReeks(null);
                    mListener.setLastPlaats(null);
                    mListener.setOplossing(null);

                }
                else {
                    Log.d("eindfragment verzending","lastplaats null");
                }

            }
        });

        //navigeren naar nieuwe reeks
        kiesReeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //controleren of het laatste dossier oplossingen heeft
                if(!tekst.toString().equals("") && tekst.toString()!=null)
                {
                    //oplossingtekst van laatste dossier toevoegen
                    mListener.setOplossing(tekst.toString());
                }

                //laatst ingevulde dossier op null zetten
                mListener.setLastDossier(null);
                //gaan naar het keuzefragment
                mListener.goToKeuzeFragment();

            }
        });


        return view;
    }

    //Mail versturen naar Adviseur
    protected void sendEmail(int lastPlaats) {

        Log.i("Send email", "");

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));

        emailIntent.setType("text/plain");
/*
        List<ResolveInfo> pkgAppsList = getActivity().getPackageManager().queryIntentActivities( emailIntent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);



//Cycle through list of apps in list and select the one that matches GMail's package name

        for (ResolveInfo resolveInfo : pkgAppsList) {

            String packageName = resolveInfo.activityInfo.packageName;

            String className = "";

            if(packageName.equals("com.google.android.gm")) {

                className = resolveInfo.activityInfo.name;

                emailIntent.setClassName(packageName, className);

            }

        }

*/

        //Lijst van apps ophalen die aan bovenstaande criteria voldoen

        emailIntent.putExtra(Intent.EXTRA_EMAIL, mListener.getEmail());

        //emailIntent.putExtra(Intent.EXTRA_CC, "EMAIL ONTVANGER");

        //emailIntent.putExtra(Intent.EXTRA_BCC, "EMAIL ONTVANGER");

        //Ophalen info klant


        Plaats plaats = mListener.getPlaats(lastPlaats);



        String vn = plaats.getVoornaam();
        String fn = plaats.getNaam();
        String plts = plaats.getStraat() + " " + plaats.getNummer() + " " + plaats.getPostcode() + " " + plaats.getGemeente();
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enquete" + fn + " " + vn);

        emailIntent.putExtra(Intent.EXTRA_TEXT, plts + "\n" + oplossing.getText());


        try {

            startActivity(Intent.createChooser(emailIntent, "Send mail..."));

            startActivity(emailIntent);

            getActivity().finish();



        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(getActivity(),"Er is geen e-mail app geïnstalleerd op uw apparaat", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);


        if(activity instanceof OnFragmentInteractionListener)
        {
            mListener =(OnFragmentInteractionListener) activity;

        }
        else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    private void goToHome()
    {
        Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        ArrayList<VragenDossier> getVragenDossiers(int dossiernr);
        Integer getLastDossier();
        void setLastDossier(Integer lastdossier);
        void setLastPlaats(Integer lastPlaats);
        void setLastVraag(Integer lastVraag);
        Plaats getPlaats(int id);
        Integer getLastPlaats();
        void setLastReeks(Integer lastReeks);
        ArrayList<AntwoordOptie> getAntwoorden(int vraagid);
        void goToInstellingen(Integer lastDossier);
        void goToKeuzeFragment();
        void setOplossing ( String oplossing);
        Dossier getDossier(int id);
        String getOplossing();
        String getEmail();

    }
}
