import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class QueryProcessor {
	private PositionalIndex pi;
    private int N;
	private ArrayList<String> fileNames;
    
    public QueryProcessor(String folder) {
        pi = new PositionalIndex(folder);
        System.out.println("Finish PI");
        N = pi.numDocs();
        fileNames = pi.getFileNames();
    }
    
    //This program will have a method named topKDocs that gets a query and an integer k as parameter and returns an
    //ArrayList consisting of top k documents that are relevant to the query.
    
    public ArrayList<String> topKDocs(String query, int k) {
    	System.out.println("==========================================");
    	System.out.println("query: " + query);

    	HashMap<Integer, Double> VSScores = pi.VSSForQueryDocs(query);
        HashMap<Integer, Double> TPScores = pi.TPForQueryDocs(query);
        PriorityQueue<Tuple> TP = new PriorityQueue<>();
        PriorityQueue<Tuple> VSS = new PriorityQueue<>();
        PriorityQueue<Tuple> Relevance = new PriorityQueue<>();

        Boolean tpStatus = pi.isOneQueryWord(query);
        double TPdefault = pi.defaultTP(query);
        
        for(int i = 0; i < N; i++) {
        	double tp;
        	if(tpStatus)
        		tp = 0.0;
        	else
        		tp = (TPScores == null || !TPScores.containsKey(i))? TPdefault : TPScores.get(i);        	
        	double vs = (VSScores == null || !VSScores.containsKey(i))? 0 : VSScores.get(i);
        	double rel = 0.6 * tp + 0.4 * vs;
        	TP.add(new Tuple(fileNames.get(i), tp));
        	VSS.add(new Tuple(fileNames.get(i), vs));
        	Relevance.add(new Tuple(fileNames.get(i), rel));
        }
                
        System.out.println("-Top " + k + " TPScore: ");
        topk(TP,10);
        System.out.println("-Top " + k + " VSScore: ");
        topk(VSS, 10);
        System.out.println("-Top " + k + " Relevance: ");
        ArrayList<String> topkRel = topk(Relevance, k);
        System.out.print("\n");
        
        return topkRel;
    }

        
    private ArrayList<String> topk( PriorityQueue<Tuple> Scores, int k) {
    	int sizeQ = Scores.size();
    	if(sizeQ == 0)
    		return null;
    	ArrayList<String> topk = new ArrayList<String>();
    	int size = k < sizeQ? k : sizeQ;
    	for(int i = 0; i < size; i++) {
    		Tuple element = Scores.poll();
    		
    		String name = element.getS();
    		double score = element.getW();
    		
    		System.out.println("  " + (i + 1) + "\tdoc: " + name + "\tscore: " + score);
    		if(name != null)
    			topk.add(name);
    	}
    	return topk;
    }


}
