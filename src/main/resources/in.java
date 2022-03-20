import java.util.ArrayList;
import java.util.HashMap;
import  java.util.List;
import java.util.Map;

public abstract class Test {

    public int a;

    public int returnTest(String str){
        int b = 1;
        b = 2;
        int c;
        String str;
        List<Integer> list = new ArrayList<>();
        list.add(1);
        A.B.C.D.a();
        while(a > b){
            if(a=b){
                a++;
            }
        }
        if(a > b){
            a = b;
        }else{
            b = a;
            b--;
            b++;
            --b;
            ++b;
        }
        if(a == b)
            b--;
        for(int i = 0;i < 10;i++){
            a = i;
            if(i==2){
                break;
            }
        }
        for(int i = 0;i < 10;i++)
            print(i);
        System.out.println(list.get(0));
        return str.length;
    }

    public static void main(String[] args) {
        int b = 1;
        int c;
        String str;
        List<Integer> list = new ArrayList<>();
        list.add(1);
        System.out.println(list.get(0));
    }
}

class Man{
    String name;
    int age;
    public Man(int age,String name){
        this.age = age;
        this.name = name;
    }
}

interface Person{
    int a = 2;
    int[] b = new int[10];
    public String str1 = null,str2="2";
    String hello();
    boolean bool = true;
    List<Integer> list = new ArrayList<>();
    String[] strs = new String[1];
    Map<String,Integer> map = new HashMap<>();
    int c = (b[1] + a)*a - b[0] + a;
    default int sum(int a,int b){
        return a+b;
    }
    Man man = new Man(20,"lbw");
}
