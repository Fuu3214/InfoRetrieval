import java.util.*;

public class Test {
	public static void testDist() {
        
        int dist = 17;
        
        ArrayList<Integer> p = new ArrayList<Integer>();
        ArrayList<Integer> r = new ArrayList<Integer>();

        for(int i = 0; i < 10; i++) {
        	int rand1 = (int) (Math.random() * 10) + 1 ;
        	int rand2 = (int) (Math.random() * 10) + 1 ;
        	p.add(rand1 * 11);
        	r.add(rand2 * 13);
        }
        p.remove(0);
        p.remove(8);
        Collections.sort(p);
        Collections.sort(r);
        System.out.println(p);
        System.out.println(r);
        
//		Method similar to merging two sorted array�� O(n)      

        int i =0;
        int j = 0;
        int sizeofR = r.size();
        int sizeofP = p.size();
        while(j < sizeofP && i < sizeofR) {
        	int pj = p.get(j);
        	int ri = r.get(i);
        	if(pj >  ri) { 
        		i++;
    		}
        	else {
        		dist = dist < (ri - pj)? dist : (ri - pj);
        		j++;
    		}
        	System.out.print(pj + "&" + ri + " ");
        }
        System.out.println("\n" + dist);
	}
    public static void testprocess() {
    	String eachLine = "ASDc,as?d,fs{asds}zxc. ;as:dsa?dzasdAAxc.)asd\"\' 123 The the 123.asdasd 12322.123 3123. asdsad.212 .1234";
    	System.out.println(eachLine);
        eachLine = eachLine.toLowerCase();
        String regex = ",|\"|\\?|\\[|\\]|'|\\{|\\}|:|;|\\(|\\)|(?<!\\d)\\.|\\.(?!\\d)";
        eachLine = eachLine.replaceAll(regex, " ");
        System.out.println(eachLine);
    }
    public static void main(String[] args) {

//        PositionalIndex pi = new PositionalIndex("IR");
//        System.out.println(pi.numDocs());
//        pi.notContain("is");
//        System.out.println(pi.docFrequency("is"));
//        System.out.println(pi.termFrequency("is", "List_of_people_from_New_Jersey.txt"));
//        System.out.println(pi.weight("is", "List_of_people_from_New_Jersey.txt"));
//        System.out.println("------------------");
//        System.out.println(pi.docFrequency("state"));
//        System.out.println(pi.termFrequency("state", "List_of_people_from_New_Jersey.txt"));
//        System.out.println(pi.weight("state", "List_of_people_from_New_Jersey.txt"));
//        System.out.println("------------------");
//        System.out.println(pi.postingsList("along"));
//        System.out.println("==================");
//		System.out.println(pi.TPScore("Major League Baseball players", "1936_Philadelphia_Phillies_season.txt"));
//		System.out.println(pi.TPForQueryDocs("Major League Baseball players").get(pi.getFileNames().indexOf("1936_Philadelphia_Phillies_season.txt")));
//		System.out.println(pi.VSScore("my name is shang da i study in iowa state university", "Juan_P%C3%A9rez_(outfielder).txt"));
//		System.out.println(pi.VSScore("my name is shang da i study in iowa state university", "Iowa.txt"));//		System.out.println(pi.VSSForQueryDocs("Maxjor League Baseball players").get(pi.getFileNames().indexOf("1936_Philadelphia_Phillies_season.txt")));
//		System.out.println("==================");
//        testDist();
//        testprocess();
//        String query = "ASDc,as?d,fs{asds}zxc. ;as:dsa?dzasdAAxc.)asd\"\' 123123.asdasd 12322.123";
//        System.out.println(pi.splitQuery(query).toString());
        
//        System.out.println(pi.TPScore("hello oh some", "BB.txt"));
//        System.out.println(pi.VSScore("wrong thing", "DD.txt"));

//        ArrayList<Integer> vq = new ArrayList<>();
//        vq.add(1);
//        vq.add(0);
//
//        ArrayList<Integer> vd = new ArrayList<>();
//        vd.add(0);
//        vd.add(1);
//
//        double numerator = vq.get(0)*vd.get(0) + vq.get(1)*vd.get(1);
//
//        double denominator = (Math.pow(vq.get(0),2)+ Math.pow(vq.get(1),2))
//                *(Math.pow(vd.get(0),2)+ Math.pow(vd.get(1),2));
//        denominator = Math.sqrt(denominator);
////        System.out.println(numerator/denominator);
//
//
        ///Users/lyusiying/Desktop/COMS535/PA4/IR    Major League Baseball players
        ///Users/lyusiying/Desktop/COMS535/PA4New/535pa4/PA4/test 1 2 3 9
		
        QueryProcessor qp = new QueryProcessor("IR");
//        qp.topKDocs("",10);
//        qp.topKDocs("Major",10);
//        qp.topKDocs("Major League",10);
//        qp.topKDocs("Major League Baseball",10);
//        qp.topKDocs("timeline of Major League Baseball players",10);
//        qp.topKDocs("National League record for assists", 10);
//        qp.topKDocs("xxxxxxx League yyyyyyyyyy for assists", 10);
//        qp.topKDocs("xxxxx yyyyyy zzzzzz aaaaaa bbbbbbb", 10);
        qp.topKDocs("iowa state", 10);
        qp.topKDocs("iowa state university", 10);
        qp.topKDocs("the position of iowa state university", 10);
        qp.topKDocs("the rank of iowa state university", 10);

        
//        System.out.println(Arrays.toString(list.toArray(new String[list.size()])));


    }
}
