package fr.Web;

import fr.Bot.GroupServer;
import fr.Bot.Serveur;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainWeb {
    public static void main(String[] args) throws Exception {
        List<GroupServer> m_serveur = new ArrayList<GroupServer>();
        List<String>pays = new ArrayList<String>();
        pays.add("EU");pays.add("GR");pays.add("EN");pays.add("ES");pays.add("FR");pays.add("IT");pays.add("PL");pays.add("GR");pays.add("EN");pays.add("ES");pays.add("FR");pays.add("IT");pays.add("PL");
        int i=0;
        String s;
        GroupServer group = null;
        Serveur srv = null;
        boolean premier=true;
        BufferedReader r = new BufferedReader(new InputStreamReader(new URL("https://www.g-status.com/game/soulworker").openStream()));
        while (!(s = r.readLine()).contains("</html>")) {
            if (s.contains("tab_title")){
                String title = s.split("<div class=\"tab_title\"><h3>")[1].split("</h3></div>")[0];
                if (!premier) m_serveur.add(group);
                else premier=false;
                group=new GroupServer(title);
            }
            if (s.contains("flag text-align-center")){
                srv=new Serveur();
                srv.setM_pays(pays.get(i));
                i++;
            }
            if(s.contains("server_name")){
                srv.setM_name(s.split("\"server_name\">")[1].split("</di")[0]);
            }
            if(s.contains("last_offline_date")){
                srv.setM_date(s.split("line_date\">")[1].split("</")[0]);
            }
            if(s.contains("div class=\"status")){
                onOff(s, srv);
                group.addServeur(srv);
            }
        }
        m_serveur.add(group);
            System.out.println("==========================================================\n\nVoici tous les serveurs : ");
        for (GroupServer si:m_serveur) {
            System.out.println("[SERVER GROUP : "+si.getM_title()+"]");
            for(int j=0;j<si.getSize();j++){
                Serveur srvtmp = si.getServeurbyId(j);
                System.out.println("["+srvtmp.getM_pays()+"]"+srvtmp.getM_name());
            }
        }
    }

    private static void onOff(String s, Serveur srv) {
        if (s.contains("ONLINE"))srv.setM_actif(true);
        else  srv.setM_actif(false);
    }
}
