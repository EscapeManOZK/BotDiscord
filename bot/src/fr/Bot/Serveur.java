package fr.Bot;

public class Serveur {
    private String m_name;
    private Boolean m_actif;

    public Serveur(String name,Boolean actif){
        m_name=name;
        m_actif=actif;
    }

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
}
