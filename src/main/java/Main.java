import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Mass;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;
import org.jscience.physics.model.RelativisticModel;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;

public class Main {

  public static void main(String[] args) {

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");

    get("/hello", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
           // attributes.put("message", "Hello World!");

            attributes.put("message", "Spark i Freemaker w akcji!");
            RelativisticModel.select();
            Amount<Mass> m = Amount.valueOf("12 GeV").to(SI.KILOGRAM);
            String massMessage = "E=mc^2: 12 GeV = " + m.toString();
            attributes.put("massMessage", massMessage);
            Amount<Energy> e = Amount.valueOf("100 kg").to(SI.GIGA(NonSI.ELECTRON_VOLT));
            String energyMessage = "E=mc^2: 100 kg = " + e.toString();
            attributes.put("energyMessage", energyMessage);
            
            return new ModelAndView(attributes, "hello.ftl");
        }, new FreeMarkerEngine());
   
    get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());
    get("/db", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

        ArrayList<String> output = new ArrayList<String>();
        while (rs.next()) {
          output.add( "Read from DB: " + rs.getTimestamp("tick"));
        }

        attributes.put("results", output);
        return new ModelAndView(attributes, "db.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());

  }

}
