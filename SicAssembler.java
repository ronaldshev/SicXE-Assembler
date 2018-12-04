// ronald shevchenko
// n01385011
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Command{
  String command, label, instruction, operand, comment, PC, address, opcode, LTORG, use = "main", useAddress, csect = "main";
  String line[];

  public Command(Command oldCommand, HashTableCodes hashTableCodes){
    label = oldCommand.getLabel();
    instruction = oldCommand.getInstruction();
    operand = oldCommand.getOperand();
    comment = oldCommand.getComment();
  }

  public Command(String command, HashTableCodes hashTableCodes){
    String[] commands = command.split("\\s+ | \\.+");
    if(commands.length == 4){
      label = commands[0];
      instruction = commands[1];
      operand = commands[2];
      comment = commands[3];
    }
    else if(commands.length == 3){
      int s[] = hashTableCodes.searchLinear(commands[0]);
      if(commands[0].equals("")){
        instruction = commands[1];
        operand = commands[2];
      }
      else if(s[0] == 1){
        instruction = commands[0];
        operand = commands[1];
        comment = commands[2];
      }
      else{
        label = commands[0];
        instruction = commands[1];
        operand = commands[2];
      }
    }
    else if(commands.length == 2){
      if(commands[0].equals("")){
        instruction = commands[1];
      }else{
        instruction = commands[0];
        operand = commands[1];
      }
    }
    else if(commands.length == 1){
      if(commands[0].equals("")){
         comment = commands[0];
      }
      else if(commands[0].charAt(0) == '.'){
        comment = commands[0];
      }
      else{
        instruction = commands[0];
      }
    }
  }

  public String setComment(String h){
    comment = "\n" + h;
    return comment;
  }

  public String getOpcode(){
    if(opcode == null)
      return "";
    else
      return opcode;
  }

  public String setOpcode(String h){
    opcode = h;
    return opcode;
  }

  public String getLabel(){
    if(label == null)
      return "";
    else
      return label;
  }
  public String setLabel(String h){
    label = h;
    return label;
  }
  public String getInstruction(){
    if(instruction == null)
      return "";
    else
      return instruction;
  }
  public String getOperand(){
    if(operand == null)
      return "";
    else
      return operand;
  }
  public String setOperand(String h){
    operand = h;
    return operand;
  }
  public String getComment(){
    if(comment == null)
      return "";
    else
      return comment;
  }
  public String setPC(String h){
    PC = h;
    return PC;
  }
  public String getPC(){
    return PC;
  }
  public String setAddress(String h){
    address = h;
    return address;
  }
  public String getAddress(){
    if(address == null)
      return null;
    else if(address == "----")
      return address;
    else if(LTORG == null)
      return address;
    else
      return LTORG;
  }
  public String setUseAddress(String h){
    useAddress = h;
    return useAddress;
  }
  public String getUseAddress(String h){
    return useAddress;
  }
  public String setUse(String h){
    use = h;
    return use;
  }
  public String getUse(){
    return use;
  }
  public String setCsect(String h){
    csect = h;
    return csect;
  }
  public String getCsect(){
    return csect;
  }
  public String setLTORG(String h){
    LTORG = h;
    return LTORG;
  }
  public String getLTORG(){
    return LTORG;
  }
  public Boolean labelExists(){
    if(label == null || label.matches(""))
      return false;
    else
      return true;
  }

}

class OpcodesItems {
   private String instruction, opcode;
   private int format, wtf;

   public OpcodesItems(String ii){
     String[] line = ii.split("\\s+");
     instruction = line[0];
     opcode = line[1];
     try{
       format = Integer.parseInt(line[2]);
       wtf = Integer.parseInt(line[3]);
     }
     catch(Exception e){
     }
   }
   public String getKey(){
      if(instruction == null)
         return null;
      else
         return instruction;
   }
   public String getOpcode(){
     return opcode;
   }
   public int getVal(){
      return format;
   }
}
class HashTableCodes{
   private OpcodesItems[] hashLinear;
   public int arraySize;

   public HashTableCodes(int size){
     arraySize = getPrime(2*size);
     hashLinear = new OpcodesItems[arraySize];
   }

   private int getPrime(int min){
      for(int j = min+1; true; j++)
         if(isPrime(j))
            return j;
   }
   private boolean isPrime(int n){
      for(int j = 2; (j*j <= n); j++)
         if(n % j == 0)
            return false;
      return true;
   }

   public int hash(String key){
      char oneChar;
      int hashVal = 0;

      if(key.length() > 0 && key.charAt(0) != '\n'){
         oneChar = key.charAt(0);
         key = key.substring(1);
         hashVal = ((int)oneChar) % arraySize;
      }
      while(key.length() > 0){
         oneChar = key.charAt(0);
         key = key.substring(1);
         if(oneChar != '\n'){
            hashVal = (hashVal * 26 + (int)oneChar) % arraySize;
         }
      }
      return hashVal;
   }

   public void insertLinear(OpcodesItems item){
      String key = item.getKey();
      int hashVal = hash(key);

      if(hashLinear[hashVal] == null){
         hashLinear[hashVal] = item;
      }
      else{
         while(hashLinear[hashVal] != null && hashLinear[hashVal].getKey() != "-1" && !(hashLinear[hashVal].getKey().equals(item.getKey()))){
            ++hashVal;
            hashVal %= arraySize;
         }
         if(hashLinear[hashVal] == null){
            hashLinear[hashVal] = item;
         }
         else if(hashLinear[hashVal].getKey().equals(item.getKey())){
            System.out.println("ERROR: Duplicate Label '" + item.getKey() + "'");
            return;
         }
      }
   }

   public int[] searchLinear(String key){
      int i = hash(key);
      int h = 1;
      int[] b = new int[4];

      while(hashLinear[i] != null){
         if(hashLinear[i].getKey().equals(key)){
            b[0] = 1;
            b[1] = i;
            b[2] = hashLinear[i].getVal();
            b[3] = Integer.parseInt(hashLinear[i].getOpcode(), 16);
            return b;
         }
         i = (i + 1) % (arraySize);
      }
      b[0] = -1;
      b[1] = i;
      return b;
   }

   public int[] searchLinearOk(String key){
      int i = hash(key);
      int h = 1;
      int[] b = new int[4];

      while(hashLinear[i] != null){
         if(hashLinear[i].getKey().equals(key)){
            b[0] = 1;
            b[1] = i;
            b[2] = hashLinear[i].getVal();
            b[3] = Integer.parseInt(hashLinear[i].getOpcode(), 16);
            return b;
         }
         i = (i + 1) % (arraySize);
      }
      b[0] = -1;
      b[1] = i;
      return b;
   }
}
class DataItem {
   private String data;
   private int val;

   public DataItem(String ii){
      if(ii != "-1"){
         String[] part = ii.split("(?<=\\D)(?=\\d)");
         if(part.length == 1){
            data = part[0];
            data = data.trim();
         }
         else{
            data = part[0];
            data = data.trim();
            val = Integer.parseInt(part[1]);
         }
      }
      else{
         data = ii;
      }
   }

   public String getKey(){
      if(data == null)
         return null;
      else
         return data;
   }

   public int getVal(){
      return val;
   }

   public String setKey(String h){
      data = h;
      return data;
   }
}
class HashFile{
   private Command[] hashLinear;
   private Command[] linear;
   private int arraySize;

   public HashFile(int size){
      arraySize = getPrime(2*size);
      hashLinear = new Command[arraySize];
      linear = new Command[arraySize];
   }

   private int getPrime(int min){
      for(int j = min+1; true; j++)
         if(isPrime(j))
            return j;
   }
   private boolean isPrime(int n){
      for(int j = 2; (j*j <= n); j++)
         if(n % j == 0)
            return false;
      return true;
   }

   public int hash(String key){
      char oneChar;
      int hashVal = 0;

      if(key.length() > 0 && key.charAt(0) != '\n'){
         oneChar = key.charAt(0);
         key = key.substring(1);
         hashVal = ((int)oneChar) % arraySize;
      }
      while(key.length() > 0){
         oneChar = key.charAt(0);
         key = key.substring(1);
         if(oneChar != '\n'){
            hashVal = (hashVal * 26 + (int)oneChar) % arraySize;
         }
      }
      return hashVal;
   }

   public void insert(Command item, int i){
     linear[i] = item;
   }

   public void printCode(){
     for(int i = 0; i < linear.length; i++){
       System.out.printf("%2s\t\t%2s\t%3s\t%3s\t%3s\n", linear[i].getAddress(), linear[i].getLabel(),
        linear[i].getInstruction(), linear[i].getOperand(), linear[i].getComment());
     }
   }

   public void insertLinear(Command item){
      String key = item.getLabel();
      int hashVal = hash(key);

      if(hashLinear[hashVal] == null){
         hashLinear[hashVal] = item;
      }
      else{
         while(hashLinear[hashVal] != null && hashLinear[hashVal].getLabel() != "-1" && !(hashLinear[hashVal].getLabel().equals(item.getLabel()))){
            ++hashVal;
            hashVal %= arraySize;
         }
         if(hashLinear[hashVal] == null){
            hashLinear[hashVal] = item;
         }
         else if(hashLinear[hashVal].getLabel().equals(item.getLabel())){
            //System.out.println("ERROR: Duplicate Label '" + item.getLabel() + "'");
            return;
         }
      }
   }

   public int[] searchLinear(String key){
      int i = hash(key);
      int h = 1;
      int[] b = new int[3];

      while(hashLinear[i] != null){
         if(hashLinear[i].getOperand().equals(key)){
            b[0] = 1;
            b[1] = i;
            b[2] = Integer.parseInt(hashLinear[i].getAddress());
            return b;
         }
         i = (i + 1) % (arraySize);
      }
      b[0] = -1;
      b[1] = i;
      return b;
   }

   public int[] searchLinearLabel(String key){
      int i = hash(key);
      int h = 1;
      int[] b = new int[4];

      while(hashLinear[i] != null){
         if(hashLinear[i].getLabel().equals(key)){
            b[0] = 1;
            b[1] = i;
            b[2] = Integer.parseInt(hashLinear[i].getAddress(), 16);
            return b;
         }
         i = (i + 1) % (arraySize);
      }
      b[0] = -1;
      b[1] = i;
      return b;
   }

   public void printHash(){
     for(int i = 0; i < hashLinear.length; i++){
       if(hashLinear[i] != null){
         System.out.printf("%2s\t\t%2s\t%3s\t%3s\t%3s\n", i, hashLinear[i].getLabel(),
          hashLinear[i].getAddress(), hashLinear[i].getUse(), hashLinear[i].getCsect());
       }
     }
   }
}

class SicAssembler{
   public static void main(String[] args) throws IOException{
      Command command;
      OpcodesItems item;
      int size = 0, line = 0, starting, temp, longestLength = 0;
      String current, current2, trueStart = "0", start = "0", use = "main", base = "";
      String lastStart = "0";
      char symbol = ' ';
      Boolean error;
      int[] s;
      int[] b;
      String fileName = "t1.txt";
      try{

        Path path = Paths.get(fileName + ".lst"); //creates .lst file
        Files.deleteIfExists(path);
        Files.createFile(path);
        byte[] strToBytes;
        String str = "";

        Scanner x = new Scanner(new File(fileName));
        Scanner y = new Scanner(new File("SICOPS.txt"));
        while(y.hasNext()){ //size of sicops file
           y.nextLine();
           size++;
        }
        HashTableCodes hashTableCodes = new HashTableCodes(size);
        size = 0;
        while(x.hasNext()){ //size of testfile
          x.nextLine();
          size++;
        }
        HashFile hashFile = new HashFile(size);
        Command[] commands = new Command[2*size];
        Command[] literals = new Command[2*size];

        y = new Scanner(new File("SICOPS.txt"));
        while(y.hasNext()){ //inserting opcodes into hashtable
          current = y.nextLine();
          item = new OpcodesItems(current);
          hashTableCodes.insertLinear(item);
        }
        x = new Scanner(new File(fileName));
        while(x.hasNext()){
           current = x.nextLine();
           command = new Command(current, hashTableCodes);
           commands[line] = command;
           line++;
           if(longestLength < command.getOperand().length())
            longestLength = command.getOperand().length();

           if(command.getInstruction() == null){
             continue;
           }else
             s = hashTableCodes.searchLinear(command.getInstruction());
           error = false;
           command.setAddress(start);
           if(!command.getOperand().equals("")){
             symbol = command.getOperand().charAt(0);
             if(symbol == 'C' || symbol == 'X'){
               literals[line] = command;
             }
           }
           if(s[0] == 1){
             temp = Integer.parseInt(start, 16) + s[2];
             start = Integer.toHexString(temp);
           }else{
            if(command.getInstruction().matches("USE|use")){
              if(command.getOperand().matches("")){
                start = lastStart;
                use = "main";
              }else{
                lastStart = start;
                start = "0";
                use = command.getOperand();
              }
            }
            else if(command.getInstruction().matches("EQU|equ")){
            }
            else if(command.getInstruction().matches("END|end")){
            }
            else if(command.getInstruction().matches("START|start")){
               start = command.getOperand();
               trueStart = command.getOperand();
               command.setAddress(start);
            }
            else if(command.getInstruction().matches("LTORG|ltorg")){
              for(int i = 0; i < literals.length; i++){
                if(literals[i] == null){
                  continue;
                }else{
                  command = new Command(literals[i], hashTableCodes);
                  command.setAddress(start);
                  symbol = command.getOperand().charAt(1);
                  if(symbol == 'C')
                    temp = Integer.parseInt(start, 16) + Integer.parseInt(Integer.toHexString(command.getOperand().length() - 3), 16);
                  else
                    temp = Integer.parseInt(start, 16) + Integer.parseInt(Integer.toHexString((command.getOperand().length() - 3)/2), 16);
                  start = Integer.toHexString(temp);
                  command.setLabel(command.getOperand());
                  command.setOperand(command.getOperand().substring(1));
                  commands[line++] = command;
                  hashFile.insertLinear(command);
                }
              }
              Arrays.fill(literals, null);
            }
            else if(command.getInstruction().matches("BASE|base")){
              base = command.getOperand();
            }
            else if(command.getInstruction().matches("RESW|resw")){
               String mul = Integer.toHexString(Integer.parseInt(command.getOperand()) * 3);
               temp = Integer.parseInt(mul, 16) + Integer.parseInt(start, 16);
               start = Integer.toHexString(temp);
             }
             else if(command.getInstruction().matches("RESB|resb")){
              temp = Integer.parseInt(start, 16) + Integer.parseInt(command.getOperand());//, 16);
              start = Integer.toHexString(temp);
             }
             else if(command.getInstruction().matches("BYTE|byte")){
              symbol = command.getOperand().charAt(0);
              if(symbol == 'C')
                temp = Integer.parseInt(start, 16) + Integer.parseInt(Integer.toHexString(command.getOperand().length() - 3), 16);
              else if(symbol == 'X')
                temp = Integer.parseInt(start, 16) + Integer.parseInt(Integer.toHexString((command.getOperand().length() - 3)/2), 16);
              else
                temp = Integer.parseInt(start, 16) + Integer.parseInt(Integer.toHexString((command.getOperand().length() - 2)/2), 16);
              start = Integer.toHexString(temp);
             }
             else if(command.getInstruction().matches("WORD|word|RSUB|rsub|J|j")){
               temp = Integer.parseInt(start, 16) + Integer.parseInt("3", 16);
               start = Integer.toHexString(temp);
             }
             else{
               error = true;
               command.setAddress("----");
               if(!command.getInstruction().equals("")){
                str = "ERROR: Invalid Mneumonic '" + command.getInstruction() + "' is an invalid command.\n";
                strToBytes = str.getBytes();
                Files.write(path, strToBytes, StandardOpenOption.APPEND);
              }
             }
          }
          if(command.labelExists() && error == false){
            command.setUse(use);
            if(command.getLabel().charAt(0) != '='){
              hashFile.insertLinear(command);
            }
          }else if(command.getAddress().matches("----"))
            continue;
          else if(command.getInstruction().matches("START|start")){
            str = "ERROR: Undefined Label At 'START'\n";
            strToBytes = str.getBytes();
            Files.write(path, strToBytes, StandardOpenOption.APPEND);
          }
          else if(command.getInstruction().matches("END|end"))
            continue;
          else{
            str = "ERROR: Undefined Label At Address " + command.getAddress() + "\n";
            strToBytes = str.getBytes();
            Files.write(path, strToBytes, StandardOpenOption.APPEND);
          }
          command.setPC(start);
        }

        /*System.out.println();
        for(Command c: commands){
          if(c == null){
            continue;
          }else{
            if(c.getAddress().equals("----") && c.getLabel().equals("") &&
                c.getInstruction().equals("") && c.getOperand().equals("")){
               System.out.printf("%3s\t%2s\n", c.getAddress(), c.getComment());
             }else{
                System.out.printf("%3s\t%2s\t%2s\t%2s\t%2s\n", c.getAddress(), c.getLabel(),
                c.getInstruction(), c.getOperand(), c.getComment());
              }
          }
        }*/

        System.out.println("\nTable Location\tLabel\tAddress\tUse\tCsect");
        hashFile.printHash();

/////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////------------project 4----------------/////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
        String fullop;
        String op;
        for(int i = 0; i < commands.length; i++){
          Boolean immediate = false;
          Boolean indirect = false;
          Boolean extended = false;
          Boolean indexed = false;
          Boolean ignore = false;
          Boolean negated = false;
          symbol = '!';
          String registers = "", ni = "00", xbpe = "2", displacement = "0";
          if(commands[i] == null){
            continue;
          }
          else{
            s = hashTableCodes.searchLinear(commands[i].getInstruction());
            if(s[0] == 1){
              // op code and NI //
              op = Integer.toHexString(s[3]);
              if(commands[i].getInstruction().charAt(commands[i].getInstruction().length() - 1) == 'R'){
                ignore = true;
                String[] split = commands[i].getOperand().split(",");
                s = hashTableCodes.searchLinear(split[0]);
                registers = Integer.toHexString(s[3]);
                s = hashTableCodes.searchLinear(split[1]);
                registers += Integer.toHexString(s[3]);
              }
              if(!commands[i].getOperand().equals("")){
                symbol = commands[i].getOperand().charAt(0);
              }
              else{
                ignore = true;
                ni = "3";
              }
              if(symbol == '@'){
                indirect = true;
                ni = "2";
              }else if(symbol == '#'){
                immediate = true;
                ni = "1";
              }else if(ignore == false){
                ni = "3";
              }
              temp = Integer.parseInt(op, 16) + Integer.parseInt(ni, 16);
              op = Integer.toHexString(temp);
              op = String.format("%02X", Integer.parseInt(op, 16));

              //xbpe
              if(commands[i].getInstruction().charAt(0) == '+'){
                extended = true;
                xbpe = "1";
                if(commands[i].getOperand().charAt(commands[i].getOperand().length() - 2) == ','){
                  indexed = true;
                  xbpe = "9";
                }
              }else if(immediate || indirect){
                xbpe = "0";
              }else{
                if(symbol == '!'){
                  xbpe = "0";
                }else{
                  if(commands[i].getOperand().charAt(commands[i].getOperand().length() - 2) == ','){
                    indexed = true;
                    xbpe = "A";
                  }
                }
              }

              //displacement
              if(extended){
                if(immediate || indirect){
                  s = hashFile.searchLinearLabel(commands[i].getOperand().substring(1));
                  if(s[0] == 1){
                    displacement = Integer.toHexString(s[2]);
                  }else{
                    displacement = commands[i].getOperand().substring(1);
                  }
                }else{
                  s = hashFile.searchLinearLabel(commands[i].getOperand());
                  if(s[0] == 1){
                    displacement = Integer.toHexString(s[2]);
                  }else{
                    displacement = commands[i].getOperand();
                  }
                }
              }else{
                if(!commands[i].getOperand().equals("")){
                  if(commands[i].getOperand().charAt(0) == '#'){
                    s = hashFile.searchLinearLabel(commands[i].getOperand().substring(1));
                    if(s[0] == 1){
                      xbpe = "2";
                      if(s[2] > Integer.parseInt(commands[i].getPC(), 16)){
                        temp =  s[2] - Integer.parseInt(commands[i].getPC(), 16);
                        displacement = Integer.toHexString(temp);
                      }else{
                        temp = Integer.parseInt(commands[i].getPC(), 16) - s[2];
                        temp = ~temp + 1;
                        displacement = Integer.toHexString(temp);
                        displacement = displacement.substring(displacement.length() - 3);
                        negated = true;
                      }
                    }else{
                      displacement = Integer.toHexString(Integer.parseInt(commands[i].getOperand().substring(1), 10));
                    }
                  }else if(commands[i].getOperand().charAt(0) == '@'){
                    s = hashFile.searchLinearLabel(commands[i].getOperand().substring(1));
                    if(s[0] == 1){
                      xbpe = "2";
                      if(s[2] > Integer.parseInt(commands[i].getPC(), 16)){
                        temp =  s[2] - Integer.parseInt(commands[i].getPC(), 16);
                        displacement = Integer.toHexString(temp);
                      }else{
                        temp = Integer.parseInt(commands[i].getPC(), 16) - s[2];
                        temp = ~temp + 1;
                        displacement = Integer.toHexString(temp);
                        displacement = displacement.substring(displacement.length() - 3);
                        negated = true;
                      }
                    }else{
                      displacement = Integer.toHexString(Integer.parseInt(commands[i].getOperand().substring(1), 10));
                    }
                  }else{
                    if(commands[i].getOperand().charAt(commands[i].getOperand().length() - 2) == ','){
                      String[] string = commands[i].getOperand().split(",");
                      s = hashFile.searchLinearLabel(string[0]);
                      if(s[0] == 1){
                        if(s[2] > Integer.parseInt(commands[i].getPC(), 16)){
                          temp =  s[2] - Integer.parseInt(commands[i].getPC(), 16);
                          displacement = Integer.toHexString(temp);
                        }else{
                          temp = Integer.parseInt(commands[i].getPC(), 16) - s[2];
                          temp = ~temp + 1;
                          displacement = Integer.toHexString(temp);
                          displacement = displacement.substring(displacement.length() - 3);
                          negated = true;
                        }
                      }
                    }else{
                      s = hashFile.searchLinearLabel(commands[i].getOperand());
                      if(s[0] == 1){
                        if(s[2] > Integer.parseInt(commands[i].getPC(), 16)){
                          temp =  s[2] - Integer.parseInt(commands[i].getPC(), 16);
                          displacement = Integer.toHexString(temp);
                        }else{
                          temp = Integer.parseInt(commands[i].getPC(), 16) - s[2];
                          temp = ~temp + 1;
                          displacement = Integer.toHexString(temp);
                          displacement = displacement.substring(displacement.length() - 3);
                          negated = true;
                        }
                      }
                    }
                  }
                }
              }

              if((temp < -2048 || temp > 2047)){
                xbpe = Integer.toHexString(Integer.parseInt(xbpe, 16) + 2);
                b = hashFile.searchLinearLabel(base);
                if(s[2] > b[2]){
                  temp =  s[2] - b[2];
                  displacement = Integer.toHexString(temp);
                }else{
                  temp = b[2] - s[2];
                  temp = ~temp + 1;
                  displacement = Integer.toHexString(temp);
                  displacement = displacement.substring(displacement.length() - 3);
                  negated = true;
                }
              }

              if(temp < -2048 || temp > 4095){
                commands[i].setComment("ERROR: Displacement out of range PC relative and for BASE value");
                continue;
              }

              if(extended){
                displacement = String.format("%05X", Integer.parseInt(displacement, 16));
              }else{
                displacement = String.format("%03X", Integer.parseInt(displacement, 16));
              }
              //full opcode
              if(registers.equals("")){
                fullop = op + xbpe + displacement;
              }else{
                fullop = op + registers;
              }
              commands[i].setOpcode(fullop);
            }else if(commands[i].getInstruction().matches("RESW|resw")){
            }else if(commands[i].getInstruction().matches("WORD|word")){
              fullop = Integer.toHexString(Integer.parseInt(commands[i].getOperand(), 10));
              commands[i].setOpcode(String.format("%06X", Integer.parseInt(fullop, 16)));
            }else if(commands[i].getInstruction().matches("BYTE|byte")){
              symbol = commands[i].getOperand().charAt(0);
              if(symbol == 'C'){
                fullop = "";
                for(int j = 2; j < commands[i].getOperand().length() - 1; j++){
                  char character = commands[i].getOperand().charAt(j);
                  int ascii = (int) character;
                  fullop += Integer.toHexString(ascii).toUpperCase();
                }
                commands[i].setOpcode(fullop);
              }else{
                //place exactly with first 7 bytes
                String[] split = commands[i].getOperand().split("'");
                fullop = split[1];
                fullop = fullop.length() < 7 ? fullop : fullop.substring(0,7);
                commands[i].setOpcode(fullop);
              }
            }
          }
        }


        ////////////////////obj file///////////////

        Path path2 = Paths.get(fileName + ".obj"); //creates .obj file
        Files.deleteIfExists(path2);
        Files.createFile(path2);
        boolean first = true;

        for(Command c: commands){
          if(c == null)
            continue;
          else{
            if(first){
              str = String.format("%06X", Integer.parseInt(trueStart, 16)) + "\n";
              str += "000000" + "\n";
              first = false;
            }else{
              if(!c.getOpcode().equals("") && !c.getInstruction().matches("RESW|resw")){
                str += c.getOpcode() + "\n";
              }else if(c.getInstruction().matches("RESW|resw")){
                str += "!\n";
                str += String.format("%06X", Integer.parseInt(c.getPC(), 16)) + "\n";
                str += "000000" + "\n";
              }else if(c.getInstruction().matches("END|end")){
                str += "!\n";
              }
            }
          }
        }

        strToBytes = str.getBytes();
        Files.write(path2, strToBytes, StandardOpenOption.APPEND);

        str = String.format("\n%-7s\t%-8s\t%-"+longestLength+"s\t%-11s\t%-"+longestLength+"s\t%-8s\n", "Address", "Opcode", "Label", "Instruction", "Operand", "Comment");
        strToBytes = str.getBytes();
        Files.write(path, strToBytes, StandardOpenOption.APPEND);

        for(Command c: commands){
          if(c == null)
            continue;
          else{
            if(c.getAddress().equals("----") && c.getLabel().equals("") && c.getInstruction().equals("") && c.getOperand().equals("")){
              str = String.format("%-16s\t%s\n", c.getAddress(), c.getComment());
              strToBytes = str.getBytes();
              Files.write(path, strToBytes, StandardOpenOption.APPEND);
            }else{
              str = String.format("%-7s\t%-8s\t%-"+longestLength+"s\t%-11s\t%-"+longestLength+"s\t%-8s\n", c.getAddress(), c.getOpcode(), c.getLabel(),
                          c.getInstruction(), c.getOperand(), c.getComment());
              strToBytes = str.getBytes();
              Files.write(path, strToBytes, StandardOpenOption.APPEND);
            }
          }
        }
      }
      catch(FileNotFoundException e){
        System.out.println("ERROR: No Input File Specified");
        System.exit(1);
      }
   }

}
