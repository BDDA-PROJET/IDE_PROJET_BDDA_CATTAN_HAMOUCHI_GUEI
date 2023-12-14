package up.mi.bdda.app.database.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import up.mi.bdda.app.database.DatabaseManager;
import up.mi.bdda.app.database.resource.TableInfo;
import up.mi.bdda.app.database.resource.ColumnInfo;
import up.mi.bdda.app.database.resource.Record;
import up.mi.bdda.app.file.FileManager;

public class DeleteCommand implements Command {
  private String resourceName;
  private List<Filtre> filters;

  public DeleteCommand(String[] args) {
    filters = new ArrayList<>();
    init(args);
  }

  private void init(String[] queries) {
    resourceName = queries[0];
    if(queries.length > 1 && queries[1].equals("WHERE")){
      for(int i = 2; i < queries.length; i++){
      conditionHandler(queries[i]);
      }
    }
  }

  private void conditionHandler(String condition){
    String operateurs[] = new String[]{"=", "<" , ">", "<=", ">=", "<>"};
    if(condition.contains("AND")){
      return ;
    }

    for (String op : operateurs) {
      if(condition.contains(op)){
        String colonne = condition.split(op)[0];
        String valeur = condition.split(op)[1];
        filters.add(new Filtre(colonne, valeur, op));
      }
      }
    }


  @Override
  public void execute() throws IOException {

    int compteur = 0;
    TableInfo resource = DatabaseManager.getSingleton().getDatabaseInfo().getTableInfo(resourceName);
    if (resource == null) {
      System.out.println("Table " + resourceName + " does not exist");
      return;
    }

    Collection<Record> records = FileManager.getSingleton().getAllRecords(resource);
   
    for (Filtre filtre : filters) {

      Predicate<? super Record> predicate = (Record record) -> {

        switch (filtre.operateur()) {
          case "=":
            record.fields().get(filtre.colonne()).value().equals(filtre.valeur());
          case "<>":
            return !(record.fields().get(filtre.colonne()).value().equals(filtre.valeur()));
          case "<":
            Object obj = record.fields().get(filtre.colonne()).value();
            Iterator <ColumnInfo> columnit = record.getResource().schema().iterator();
            while(columnit.hasNext()){
              ColumnInfo ci = columnit.next();
              if(ci.name().equals(filtre.colonne())){
                Object obj2 = ci.type().parse(obj.toString());
                Object obj3 = ci.type().parse(filtre.valeur()); 
                switch(ci.type().name()){
                  case "INT" :
                    return(int)obj2 < (int)obj3;
                  case "FLOAT":
                    return (float)obj2 < (float)obj3;
                  default:
                    break;
                }
              }
            }
          case ">":
            Object obj4 = record.fields().get(filtre.colonne()).value();
            Iterator <ColumnInfo> columnit2 = record.getResource().schema().iterator();
            while(columnit2.hasNext()){
              ColumnInfo ci = columnit2.next();
              if(ci.name().equals(filtre.colonne())){
                Object obj5 = ci.type().parse(obj4.toString());
                Object obj6 = ci.type().parse(filtre.valeur()); 
                switch(ci.type().name()){
                  case "INT" :
                    return(int)obj5 > (int)obj6;
                  case "FLOAT":
                    return (float)obj5 > (float)obj6;
                  default:
                    break;
                }
              }
            }
          case "<=":
            Object obj7 = record.fields().get(filtre.colonne()).value();
            Iterator <ColumnInfo> columnit3 = record.getResource().schema().iterator();
            while(columnit3.hasNext()){
              ColumnInfo ci = columnit3.next();
              if(ci.name().equals(filtre.colonne())){
                Object obj8 = ci.type().parse(obj7.toString());
                Object obj9 = ci.type().parse(filtre.valeur()); 
                switch(ci.type().name()){
                  case "INT" :
                    return(int)obj8 <= (int)obj9;
                  case "FLOAT":
                    return (float)obj8 <= (float)obj9;
                  default:
                    break;
                }
              }
            }
          case ">=":
            Object obj10 = record.fields().get(filtre.colonne()).value();
            Iterator <ColumnInfo> columnit4 = record.getResource().schema().iterator();
            while(columnit4.hasNext()){
              ColumnInfo ci = columnit4.next();
              if(ci.name().equals(filtre.colonne())){
                Object obj11 = ci.type().parse(obj10.toString());
                Object obj12 = ci.type().parse(filtre.valeur()); 
                switch(ci.type().name()){
                  case "INT" :
                    return(int)obj11 >= (int)obj12;
                  case "FLOAT":
                    return (float)obj11 >= (float)obj12;
                  default:
                    break;
                }
              }
            }
          
            default:
          return false;
        }
      };

      if(records.removeIf(predicate)){
        compteur ++;
      }
      
    }



    System.out.println("Nombre de record supprimé() : " + compteur);
  }
}


