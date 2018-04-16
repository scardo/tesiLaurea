package Data;

import Data.Stats.BarPlotData;
import Data.Stats.PieData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import org.postgis.LineString;
import org.postgis.PGbox2d;
import org.postgis.PGgeometry;
import org.postgis.Point;

//------------lat = x, lon=y
/**
 *
 * @author alessandropandolfo
 */
@SuppressWarnings("UnusedAssignment")
public class Datasource {

    private static Datasource ds = null;

    private final String user = "parseDouble";
    private final String passwd = "scardo92";

    // URL per la connessione alla base di dati e' formato dai seguenti
    // componenti: <protocollo>://<hosource()st del server>/<nome base di dati>.
    //private final String url = "jdbc:postgresql://localhost:5432/spatialDB";
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    
    // Driver da utilizzare per la connessione e l'esecuzione delle query.
    private final String driver = "org.postgresql.Driver";

    private Datasource() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static synchronized Datasource getInstance() {
        if (!(ds instanceof Datasource)) {
            ds = new Datasource();
        }
        return ds;
    }

    public ArrayList<Trajectory> getTrajectoryFromDB(String query) {

        // Dichiarazione delle variabili necessarie
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Trajectory> out = new ArrayList<>();

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                out.add(makeTrajectory(rs));
            }
            con.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    //udiamo linestring e non polygon perchè la query dice che servono più punti per fare un polygon
    public ArrayList<Trajectory> getTrajectoryFromDB(LineString geom, double[] timeRange) {
        // Dichiarazione delle variabili necessarie
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<Trajectory> out = new ArrayList<>();

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

            String query
                    = "SELECT distinct \"trajID\" "
                    + "FROM \"public\".\"Traj\", (select st_makepolygon(st_geomfromtext('" + geom.toString() + "',4326 ))as bound) as bounds "
                    + "WHERE geom && bounds.bound AND "
                    + "st_intersects( geom, bounds.bound) ";
            if (timeRange != null) {
                query = "select distinct \"trajID\" "
                        + "from ( "
                        + " select distinct \"trajID\", (st_dumpPoints(\"public\".\"Traj\" .geom)).geom as times "
                        + " from \"public\".\"Traj\"  "
                        + " where \"trajID\" in ( "
                        + query
                        + " ))as points ";
                if (timeRange[0] != 0 || timeRange[1] != 0) {
                    if (timeRange[0] != 0) {
                        query += " where st_m(times) >= " + timeRange[0];
                        if (timeRange[1] != 0) {
                            query += " and st_m(times)<= " + timeRange[1] + " ";
                        }
                    } else {
                        query += " where st_m(times)<= " + timeRange[1] + " ";
                    }
                }
            }

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                out.add(makeTrajectory(rs));
            }
            con.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    private Trajectory makeTrajectory(ResultSet rs) throws SQLException {

        String id = rs.getString(1);

        return new Trajectory(id);
    }

    @SuppressWarnings("Convert2Diamond")
    public ArrayList<double[]> pointsOfTrajectory(Trajectory t, String geom) {
        ArrayList<double[]> points = new ArrayList<double[]>();
        // Dichiarazione delle variabili necessarie
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();
            String query = "select \"points\".\"trajID\", (st_dumpPoints(\"public\".\"Traj\".geom)).geom "
                    + "from ( "
                    + "select \"public\".\"Traj\".\"trajID\", st_intersection(\"public\".\"Traj\" .geom, st_makepolygon(st_geomfromtext(' " + geom.toString() + " ',4326))) as segm "
                    + "from \"public\".\"Traj\" "
                    + "where \"trajID\"='" + t.getId_t() + "') as points inner join \"public\".\"Traj\" on (points.\"trajID\"=\"public\".\"Traj\".\"trajID\")";

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                double pt[] = new double[5];
                pt[0] = rs.getDouble(1);

                pt[1] = ((PGgeometry) rs.getObject(2)).getGeometry().getFirstPoint().getY();
                pt[2] = ((PGgeometry) rs.getObject(2)).getGeometry().getFirstPoint().getX();
                pt[3] = ((PGgeometry) rs.getObject(2)).getGeometry().getFirstPoint().getZ();
                pt[4] = ((PGgeometry) rs.getObject(2)).getGeometry().getFirstPoint().getM();
                points.add(pt);
            }

            con.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return points;
    }

    public double[] getTimeBorderOfTrajectory(Trajectory tra, LineString geom) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        double bt[] = new double[2];
        Point point;

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();
            int j = 0;
            bt[0] = Double.MAX_VALUE;
            bt[1] = 0;
            String query = null;
            if (geom == null) {
                query = "select max(st_m(points.geom_dump)), min(st_m(points.geom_dump)) "
                        + "from ( "
                        + "select (st_dumpPoints(segment.geom)).geom as geom_dump, geom "
                        + "from (select \"public\".\"Traj\".\"trajID\", geom  "
                        + "from \"public\".\"Traj\"  ) as segment "
                        + "where \"trajID\"='" + tra.getId_t() + "')as points ";
            } else {
                query = "select max(st_m(points.geom_dump)), min(st_m(points.geom_dump)) "
                        + "from ( "
                        + "select (st_dumpPoints(segment.geom)).geom as geom_dump, geom "
                        + "from (select \"public\".\"Traj\".\"trajID\", geom, st_intersection(\"public\".\"Traj\" .geom, "
                        + "st_makepolygon(st_geomfromtext(' " + geom.toString() + " ',4326))) as segm "
                        + "from \"public\".\"Traj\"  ) as segment "
                        + "where \"trajID\"='" + tra.getId_t() + "')as points ";

            }
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                bt[1] = rs.getDouble(1);
                bt[0] = rs.getDouble(2);
            }

            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return bt;
    }

    public double[] getTimeBorder(ArrayList<Trajectory> tra, LineString geom) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        double bt[] = new double[2];
        Point point;

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();
            ArrayList<Trajectory> trajectories = tra;
            int j = 0;
            bt[0] = Double.MAX_VALUE;
            bt[1] = 0;
            String query = null;
            for (Trajectory t : trajectories) {
                if (j % 1000 == 0) {
                    if (query != null) {
                        query = query.substring(0, query.length() - 3);
                        query += " )as points ";

                        rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            bt[1] = (rs.getDouble(1) > bt[1]) ? rs.getDouble(1) : bt[1];
                            bt[0] = (rs.getDouble(2) < bt[0]) ? rs.getDouble(2) : bt[0];
                        }
                    }
                    query = "select max(st_m(points.geom_dump)), min(st_m(points.geom_dump)) "
                            + "from ( "
                            + "select (st_dumpPoints(segment.geom)).geom as geom_dump, geom "
                            + "from (select \"public\".\"Traj\".\"trajID\", geom, st_intersection(\"public\".\"Traj\" .geom, "
                            + "st_makepolygon(st_geomfromtext(' " + geom.toString() + " ',4326))) as segm "
                            + "from \"public\".\"Traj\"  ) as segment ";
                    query += " where ";
                }

                query += " \"trajID\"='" + t.getId_t() + "' OR";
                j++;
            }
            if (tra != null && tra.size() > 0) {
                query = query.substring(0, query.length() - 3);
                query += " )as points ";
                rs = stmt.executeQuery(query);

                if (rs.next()) {
                    bt[1] = (rs.getDouble(1) > bt[1]) ? rs.getDouble(1) : bt[1];
                    bt[0] = (rs.getDouble(2) < bt[0]) ? rs.getDouble(2) : bt[0];
                }
            }
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return bt;
    }

    public double[] getSpatialBorder(ArrayList<Trajectory> tra) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        double bs[] = new double[4];
        Point point;

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();
            String query = "select ST_Extent(\"public\".\"Traj\" .geom) "
                    + "from \"public\".\"Traj\"  ";

            if (tra != null && tra.size() > 0) {
                query += " where ";
                for (Trajectory t : tra) {
                    query += " \"trajID\"='" + t.getId_t() + "' OR";
                }
                query = query.substring(0, query.length() - 3);
            }
            query += " ";
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                point = ((PGbox2d) rs.getObject(1)).getLLB();
                bs[3] = point.getX();
                bs[2] = point.getY();
                point = ((PGbox2d) rs.getObject(1)).getURT();
                bs[1] = point.getX();
                bs[0] = point.getY();

            }
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return bs;
    }

    public Trajectory getSelectedTra(Point punto, ArrayList<Trajectory> t) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        Trajectory trajectory = null;
        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();
            String query = "select distinct \"trajID\" "
                    + " from \"public\".\"Traj\"  "
                    + " where st_DWithin(geom, st_geomfromtext('" + punto.toString() + "', 4326), "
                    + " 0.0001 ) ";
            if (t != null && t.size() > 0) {
                query += " and ( ";
                for (Trajectory traj : t) {
                    query += " \"trajID\"= " + traj.getId_t() + " OR ";
                }
                query = query.substring(0, query.length() - 3);
                query += " ) ";
            }

            rs = stmt.executeQuery(query);

            if (rs.next()) {
                trajectory = makeTrajectory(rs);
            }
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return trajectory;
    }

    @SuppressWarnings("Convert2Diamond")
    public ArrayList<Trajectory> spaceIntersect(Trajectory tr) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList<Trajectory> out = new ArrayList<Trajectory>();

        try {
            PGgeometry geom = tr.getGeometry();
            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

            String query = "SELECT \"trajID\" FROM \"public\".\"Traj\" , ( "
                    + "select * from \"public\".\"Traj\" where \"trajID\"='" + tr.getId_t() + "') as ge\n"
                    + "where \"trajID\"!='" + tr.getId_t() + "' AND \"Traj\".geog && ge.geog AND \"Traj\".geom && ge.geom AND st_distance(\"Traj\".geog, ge.geog)<10 ";

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                out.add(makeTrajectory(rs));
            }
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }


    @SuppressWarnings("Convert2Diamond")
    public ArrayList<Trajectory> spacetimeIntersect(Trajectory tr) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList<Trajectory> out = new ArrayList<Trajectory>();

        try {
            PGgeometry geom = tr.getGeometry();
            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

            String query = "Select t1.\"trajID\"\n"
                    + "from (\n"
                    + "	Select allP.\"trajID\", allP.geom, allP.geog\n"
                    + "	from (\n"
                    + "		select \"trajID\", (ST_DumpPoints(geom)).geom as pp, geom, geog\n"
                    + "		from \"public\".\"Traj\"\n"
                    + "		where \"trajID\"!='"+tr.getId_t()+"'\n"
                    + "	     ) as allP,\n"
                    + "	     (\n"
                    + "		select \"trajID\", (ST_DumpPoints(geom)).geom as pp, geom, geog\n"
                    + "		from \"public\".\"Traj\"\n"
                    + "		where \"trajID\"='"+tr.getId_t()+"'\n"
                    + "	     ) as selP\n"
                    + "	Where ST_M(allP.pp)>ST_M(selP.pp)-5 and ST_M(allP.pp)<ST_M(selP.pp)+5\n"
                    + "     ) as t1, \n"
                    + "     \"public\".\"Traj\" as t2\n"
                    + "where t2.\"trajID\"='66566' AND t1.geom && t2.geom AND t1.geog && t2.geog AND ST_Distance(t1.geog,t2.geog)<10";
            
            
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                out.add(makeTrajectory(rs));
            }
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    public PGgeometry getGeom(Trajectory tr) {
        PGgeometry out = new PGgeometry();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

            String query = "SELECT \"geom\" from \"public\".\"Traj\" where \"trajID\"= ' " + tr.getId_t() + " ' ";

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                out = (PGgeometry) rs.getObject("geom");
            }
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }
    
    public PieData getWeekDistribution(LineString geom, double dayTime[]){
        PieData out=null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        double sum=0;
        String days[]={"Mon - 0%","Tue - 0%","Wed - 0%","Thu - 0%","Fri - 0%","Sat - 0%","Sun - 0%"};
        Double perc[]={0.0,0.0,0.0,0.0,0.0,0.0,0.0};
        
        ArrayList<String> l=new ArrayList<>();
        ArrayList<Double> i = new ArrayList<>();
   
        
        
        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

          
            
            
            String query 
                ="select count(\"g\".\"geom\"), to_char(to_timestamp(ST_M(ST_AsText(ST_Startpoint(\"g\".\"geom\")))/1000)::date, 'Day') as days\n"
                +"from \n"
                +"	(SELECT distinct \"geom\" \n"
                +"	FROM \"public\".\"Traj\", (select st_makepolygon(st_geomfromtext('" + geom.toString() + "',4326 ))as bound) as bounds \n"
                +"  WHERE geom && bounds.bound AND st_intersects( geom, bounds.bound) "
                +       " AND ST_M(ST_AsText(ST_Startpoint(\"public\".\"Traj\".\"geom\")))>="+ (long)dayTime[0] 
                +       " AND ST_M(ST_AsText(ST_Startpoint(\"public\".\"Traj\".\"geom\")))<="+ (long)dayTime[1]    
                + " ) as \"g\" \n"
                +"group by days;";

            rs = stmt.executeQuery(query);

            while (rs.next()) {
                l.add(((String)rs.getObject(2)).substring(0, 3));
                
                i.add(((Long)rs.getObject(1)).doubleValue());
                sum+=((Long)rs.getObject(1)).doubleValue();
            }
            String p;
            for (int j = 0; j < l.size(); j++) {
                switch(l.get(j)){
                    case "Mon":
                        p=((i.get(j)/sum)*100)+"";
                        days[0]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[0]=i.get(j);
                        break;
                    case "Tue":
                        p=((i.get(j)/sum)*100)+"";
                        days[1]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[1]=i.get(j);
                        break;
                    case "Wed":
                        p=((i.get(j)/sum)*100)+"";
                        days[2]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[2]=i.get(j);
                        break;
                    case "Thu":
                        p=((i.get(j)/sum)*100)+"";
                        days[3]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[3]=i.get(j);
                        break;
                    case "Fri":
                        p=((i.get(j)/sum)*100)+"";
                        days[4]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[4]=i.get(j);
                        break;
                    case "Sat":
                        p=((i.get(j)/sum)*100)+"";
                        days[5]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[5]=i.get(j);
                        break;
                    case "Sun":
                        p=((i.get(j)/sum)*100)+"";
                        days[6]=l.get(j)+" - "+p.substring(0, 4)+"%";
                        perc[6]=i.get(j);
                        break;
                }
                
            }
      
            out=new PieData(days,perc,(int)sum);
            
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return out;
    }

    public BarPlotData getHourDistribution(LineString geom, double[] dayTime, boolean start) {
        
        BarPlotData out=null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        
        ArrayList<String> l=new ArrayList<>();
        ArrayList<Double> i = new ArrayList<>();
   
        
        
        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

          
            
            
            String query="";
            if(start){
                query="select count(\"g\".\"geom\"), to_char(to_timestamp(ST_M(ST_AsText(ST_Startpoint(\"g\".\"geom\")))/1000)::time, 'HH24') as hours\n";
            }else{
                query="select count(\"g\".\"geom\"), to_char(to_timestamp(ST_M(ST_AsText(ST_EndPoint(\"g\".\"geom\")))/1000)::time, 'HH24') as hours\n";
            }
            
            query=query    
                +"from \n"
                +"	(SELECT distinct \"geom\" \n"
                +"	FROM \"public\".\"Traj\", (select st_makepolygon(st_geomfromtext('" + geom.toString() + "',4326 ))as bound) as bounds \n"
                +"  WHERE geom && bounds.bound AND st_intersects( geom, bounds.bound) ";
            if(start){
                query=query 
                    +       " AND ST_M(ST_AsText(ST_Startpoint(\"public\".\"Traj\".\"geom\")))>="+ (long)dayTime[0] 
                    +       " AND ST_M(ST_AsText(ST_Startpoint(\"public\".\"Traj\".\"geom\")))<="+ (long)dayTime[1]    
                    + " ) as \"g\" \n"
                    +"group by hours \n"
                    +"order by hours";
            }else{
                query=query 
                    +       " AND ST_M(ST_AsText(ST_Endpoint(\"public\".\"Traj\".\"geom\")))>="+ (long)dayTime[0] 
                    +       " AND ST_M(ST_AsText(ST_Endpoint(\"public\".\"Traj\".\"geom\")))<="+ (long)dayTime[1]    
                    + " ) as \"g\" \n"
                    +"group by hours \n"
                    +"order by hours";
            }
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                l.add((String)rs.getObject(2));
                
                i.add(((Long)rs.getObject(1)).doubleValue());
                
            }
            
      
            out=new BarPlotData(i.toArray(new Double[i.size()]),l.toArray(new String[l.size()]));
            
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return out;
        
    }
    
    public int getNumTraj(Point p[], LinkedList<Integer> trajid){

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int out=0;
        LineString geom=new LineString();
        try {
            geom = (LineString) PGgeometry.geomFromString("LINESTRING ("
                    + p[0].x + " " + p[0].y + ", "
                    + p[0].x + " " + p[1].y + ", "
                    + p[1].x + " " + p[1].y + ", "
                    + p[1].x + " " + p[0].y + ", "
                    + p[0].x + " " + p[0].y + " "
                    + ")");
        } catch (SQLException ex) {
            Logger.getLogger(Datasource.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

          
            
            
            String query 
                ="select count(\"g\".\"trajID\")\n"
                +"from \n"
                +"	(SELECT distinct \"trajID\" \n"
                +"	FROM \"public\".\"Traj\", (select st_makepolygon(st_geomfromtext('" + geom.toString() + "',4326 ))as bound) as bounds \n"
                +"      WHERE geom && bounds.bound AND st_intersects( geom, bounds.bound) AND (false ";
            for (Integer integer : trajid) {        
                query=query+" OR \"trajID\"= "+integer;
                
            }
            
            
            query=query+ " )) as \"g\" \n";
            
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                out=Math.toIntExact((long)rs.getObject(1));
            }        
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        return out;
    }

    public LinkedList<Integer> getTrajIDFiltered(Point p[], double dayTime[]){

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Integer> out=new LinkedList<Integer>();
        LineString geom=new LineString();
        try {
            geom = (LineString) PGgeometry.geomFromString("LINESTRING ("
                    + p[0].x + " " + p[0].y + ", "
                    + p[0].x + " " + p[1].y + ", "
                    + p[1].x + " " + p[1].y + ", "
                    + p[1].x + " " + p[0].y + ", "
                    + p[0].x + " " + p[0].y + " "
                    + ")");
        } catch (SQLException ex) {
            Logger.getLogger(Datasource.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

            con = DriverManager.getConnection(url, user, passwd);
            ((org.postgresql.PGConnection) con).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
            ((org.postgresql.PGConnection) con).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));
            stmt = con.createStatement();

           
            
            
            String query 
                ="SELECT distinct \"trajID\" \n"
                +"FROM \"public\".\"Traj\", (select st_makepolygon(st_geomfromtext('" + geom.toString() + "',4326 ))as bound) as bounds \n"
                +"        WHERE geom && bounds.bound AND st_intersects( geom, bounds.bound) "
                +       " AND ((ST_M(ST_AsText(ST_Startpoint(\"public\".\"Traj\".\"geom\")))>="+ (long)dayTime[0] 
                +       " AND ST_M(ST_AsText(ST_Startpoint(\"public\".\"Traj\".\"geom\")))<="+ (long)dayTime[1]+")"             
                +       " OR (ST_M(ST_AsText(ST_Endpoint(\"public\".\"Traj\".\"geom\")))>="+ (long)dayTime[0] 
                +       " AND ST_M(ST_AsText(ST_Endpoint(\"public\".\"Traj\".\"geom\")))<="+ (long)dayTime[1] +")) ";
            
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                out.add((Integer)rs.getObject(1));
            }        
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Datasource.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        
        return out;
    }
}

