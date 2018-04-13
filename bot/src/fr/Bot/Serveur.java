package fr.Bot;

public class Serveur {
    private String m_name;

    private String m_pays;

    private String m_date;
    private Boolean m_actif;
    public Serveur(){}

    public Serveur(String name,String date,Boolean actif,String pays){
        m_name=name;
        m_actif=actif;
        m_date=date;
        m_pays=pays;
    }
    public String getM_date() {return m_date;}

    public void setM_date(String m_date) {this.m_date = m_date;}

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public Boolean getM_actif() {
        return m_actif;
    }

    public void setM_actif(Boolean m_actif) {
        this.m_actif = m_actif;
    }

    public String getM_pays() {
        return m_pays;
    }

    public void setM_pays(String m_pays) {
        this.m_pays = m_pays;
    }
}
