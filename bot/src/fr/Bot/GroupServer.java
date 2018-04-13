package fr.Bot;

import java.util.ArrayList;
import java.util.List;

public class GroupServer {
    private String m_title;

    private List<Serveur> m_serveur;
    public GroupServer(String title){
        m_title=title;
        m_serveur= new ArrayList<Serveur>();
    }
    public void addServeur(Serveur s){
        m_serveur.add(s);
    }

    public Serveur getServeurbyName(String name){
        boolean find = false;
        Serveur tmp = null;
        for (Serveur s:m_serveur) {
            String name_srv=s.getM_pays();
            if(!find&&name_srv.equals(name)){
                find=true;
                tmp=s;
            }
        }
        return tmp;
    }

    public Serveur getServeurbyId(int id){
        return m_serveur.get(id);
    }

    public int getSize(){
        return m_serveur.size();
    }

    public List<Serveur> getServeurON(){
        List<Serveur> s = new ArrayList<Serveur>();
        for (Serveur stmp: m_serveur) {
            if(stmp.getM_actif())  s.add(stmp);
        }
        return s;
    }

    public List<Serveur> getServeurOFF(){
        List<Serveur> s = new ArrayList<Serveur>();
        for (Serveur stmp: m_serveur) {
            if(!stmp.getM_actif())  s.add(stmp);
        }
        return s;
    }

    public String getM_title() {
        return m_title;
    }

    public void setM_title(String m_title) {
        this.m_title = m_title;
    }
}
