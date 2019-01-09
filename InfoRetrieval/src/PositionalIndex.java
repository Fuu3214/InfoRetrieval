import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class PositionalIndex {
    private HashMap<String, Integer> files;//search in hash map: O(1), ArrayList O(N)
    private ArrayList<String> fileNames;
    private HashMap<String, Integer> terms;
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> posting;
    private int N;
    double[] l2NormVd;
    
    public PositionalIndex(String folder) {
        this.files = new HashMap<>();
        this.fileNames = new ArrayList<String>();
        this.terms = new HashMap<>();
        this.posting = new HashMap<>();
        preprocessing(folder);
        this.N = files.size();
        l2NormVd = L2Norms();
    }

    //Returns the number of times term appears in doc.
    public int termFrequency(String term, String Doc) {

		if(!terms.containsKey(term)|| !files.containsKey(Doc))
			return 0;
        int wordInd = terms.get(term);
        int fileInd = files.get(Doc);
        
        HashMap<Integer, ArrayList<Integer>> map = posting.get(wordInd);
        if (!map.containsKey(fileInd)) {
            return 0;
        }
        return map.get(fileInd).size();
    }
    //Returns the number of documents in which term appears.
    public int docFrequency(String term) {
		if(!terms.containsKey(term))
			return 0;
        int wordInd = terms.get(term);
        HashMap<Integer, ArrayList<Integer>> map = posting.get(wordInd);
        return map.size();
    }
    //Returns string representation of the postings(t). The returned String must be in following format.
    public String postingsList(String t) {
    	if(!terms.containsKey(t))
    		return null;
        int wordInd = terms.get(t);
        String postings = "[";
        Set<Integer> keys = posting.get(wordInd).keySet();
        Iterator<Integer> itr = keys.iterator();

        while (itr.hasNext()) {
            int fileInd = itr.next();
            String s = "<";
            s = s + fileNames.get(fileInd) + " : ";
            ArrayList<Integer> q = posting.get(wordInd).get(fileInd);
            int size = q.size();
            for(int i = 0; i < size; i++) {
            	s = s + q.get(i) + ",";
            }
            s = s.substring(0, s.length()-1) + ">, ";
            postings = postings + s;
        }
        postings = postings.substring(0, postings.length()-2) + "]";
        return postings;
    }
    
    public void notContain(String t) {
    	if(!terms.containsKey(t))
    		return;
        int wordInd = terms.get(t);
        HashMap<Integer, ArrayList<Integer>> tmp = posting.get(wordInd);
        
        for(String str : fileNames) {
        	if(!tmp.containsKey(files.get(str))) {
        		System.out.println(str);
        	}
        }
    }

    //Returns the W of term t in document d
    public double weight(String t, String d) {
        //TFij is the frequency of ti in dj
        double TFij = termFrequency(t, d);
        if(TFij == 0)
        	return 0.0;
        //dfti is the number of documents in which ti appears.
        double dfti = docFrequency(t);
        return Math.sqrt(TFij) * Math.log10(N/dfti);
    }


    public double TPScore(String query, String doc) {
        ArrayList<String> qWords = splitQuery(query);
        int l = qWords.size();
        int sum = 0;
        //If q has exactly one term, then T P Score(q, d) = 0
        if (l == 1) {
            return 0.0;
        }
        for (int i = 0; i < l-1; i++) {
            String t1 = qWords.get(i);
            String t2 = qWords.get(i+1);
            sum = sum + distd(t1, t2, doc);
        }
//        System.out.println("sum: "+ sum);
        return (double)l/(double)sum;
    }
    public Boolean isOneQueryWord(String query) {
    	ArrayList<String> qWords = splitQuery(query);
    	int l = qWords.size();
    	if (l <= 1)
            return true;
    	else
    		return false;
    }
    public HashMap<Integer, Double> TPForQueryDocs(String query) {
    	System.out.println("Computing TPScore for query related docs");
//    	System.out.println(qWords);
    	HashMap<Integer, Double> S = new HashMap<>();

        ArrayList<String> qWords = splitQuery(query);
        int l = qWords.size();
        //If q has exactly one term, then T P Score(q, d) = 0
        if (l <= 1) {
            return null;
        }
        HashSet<Integer> keySet = new HashSet<>();
        for(int i = 0; i < l; i++) {
        	String term = qWords.get(i);
            if(!terms.containsKey(term)) 
            	continue;
            int wordInd = terms.get(term);
        	keySet.addAll(posting.get(wordInd).keySet());
        }

        if(keySet != null) {
	        for (int i = 0; i < l-1; i++) {
	            String t1 = qWords.get(i);
	            String t2 = qWords.get(i+1);
		    		for(int docID : keySet) {
		    			String docName = fileNames.get(docID);
		    			double s = distd(t1, t2, docName);
	//    	    			System.out.println("--" + "query: " + qTerm + " doc: " + docName +  " = " + weight(qTerm, docName));
		    			if(S.containsKey(docID)) 
		    				s += S.get(docID);
						S.put(docID, s);
		    		}
	
	    	}
        }
    	for(int docID : S.keySet()) {
    		double s = S.get(docID);
    		s = (double)l/s;
    		S.put(docID, s);
    	}
    	System.out.println("-finished");
        return S;
    }


    public double VSScore(String query, String docName) {
//    	sparse vector multiplication
    	double curL2NormVd = l2NormVd[files.get(docName)];
    	ArrayList<String>qWords = splitQuery(query);
//    	System.out.println(qWords);
    	int qSize = qWords.size();
    	double l2NormVq = 0.0;
    	double s = 0.0;
    	for(int i = 0; i < qSize;  i++) {
    		String qTerm = qWords.get(i);
    		double qweight = qWeight(qTerm, qWords);
//    		System.out.println(qTerm + " " + qweight + " " + weight(qTerm, docName));
    		l2NormVq += Math.pow(qweight, 2);
			s += weight(qTerm, docName) * qweight;
    	}
    	l2NormVq = Math.sqrt(l2NormVq);
		s = s/(l2NormVq * curL2NormVd);
        return s;
    }
    
    public HashMap<Integer, Double> VSSForQueryDocs(String query) {
    	System.out.println("Computing VSScore for query related docs");
    	ArrayList<String>qWords = splitQuery(query);
//    	System.out.println(qWords);
    	HashMap<Integer, Double> S = new HashMap<>();

    	int qSize = qWords.size();
    	double l2NormVq = 0.0;
    	for(int i = 0; i < qSize;  i++) {
    		String qTerm = qWords.get(i);
    		double qweight = qWeight(qTerm, qWords);
    		l2NormVq += Math.pow(qweight, 2);
    		
    		if(!terms.containsKey(qTerm))
    			continue;
    		int wordInd = terms.get(qTerm);

    		HashMap<Integer, ArrayList<Integer>> postingOfTerm = posting.get(wordInd);
    		if(postingOfTerm != null)
	    		for(int docID : postingOfTerm.keySet()) {
	    			String docName = fileNames.get(docID);
	    			double s = weight(qTerm, docName) * qweight;
//	    			System.out.println("--" + "query: " + qTerm + " doc: " + docName +  " = " + weight(qTerm, docName));
	    			if(S.containsKey(docID)) 
	    				s += S.get(docID);
					S.put(docID, s);
	    		}
    	}
    	l2NormVq = Math.sqrt(l2NormVq);
    	for(int docID : S.keySet()) {
    		double s = S.get(docID);
    		s = s/(l2NormVq * l2NormVd[docID]);
    		S.put(docID, s);
    	}
		System.out.println("-finished");
        return S;
    }
    
    public double Relevance(String query, String doc) {
//        System.out.println(doc+" TPS "+TPScore(query, doc) + " VSS "+VSScore(query, doc));
        return 0.6*TPScore(query, doc) + 0.4*VSScore(query, doc);
    }
    
    public ArrayList<String> getFileNames() {
    	return fileNames;
    }
    
    public int numDocs() {
    	return N;
    }
    public double defaultTP(String query) {
//    	when 2 query words are both not in document, TP score is l/((l-1)*17)
    	ArrayList<String> qWords = splitQuery(query);
    	int l = qWords.size();
    	return (double)l/((l-1)*17);
    }

    private void preprocessing(String folder) {
    	System.out.println("Preprocessing");
        File file = new File(folder);
        File[] temp = file.listFiles();
        int count = 0;
        int numFiles = temp.length;
        for (int i = 0; i < temp.length; i ++) {
            int pos = 0;
            File f = temp[i];
            String fName = f.getName();
            BufferedReader inFile = null;
            if (fName.contains(".txt")) {
            	int docID = files.size();
            	files.put(fName, files.size());
            	fileNames.add(fName);
            	
	            try {
	            	InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "UTF-8");
	                inFile = new BufferedReader(isr);
                	String eachLine = null;
	                while ( (eachLine = inFile.readLine()) != null) {
	                    
	                    //remove symbols and keep decimals
	                    eachLine = processLine(eachLine);             	
	
	                    StringTokenizer getWord = new StringTokenizer(eachLine);
	                    while (getWord.hasMoreTokens()) {
	//                      if run keepDecimal() here then "abc.123"->"abc 123"
	//                    	but should be "abc", "123"
	                        String word = getWord.nextToken().trim();
	                        	
	                        if (!word.isEmpty()) {
	                            // add in words collection
	                            if (!terms.containsKey(word)) {
	                                terms.put(word, terms.size());
	                            }
	
	                            // add in term collection
	                            int wordInd = terms.get(word);
	                            pos++;
	                            if (!posting.containsKey(wordInd)) {
	                            	ArrayList<Integer> newDoc = new ArrayList<>();
	                            	HashMap<Integer, ArrayList<Integer>> newWord = new HashMap<>();
	                            	newDoc.add(pos);
	                            	newWord.put(docID, newDoc);
	                                posting.put(wordInd, newWord);
	                            } else {
	                            	HashMap<Integer, ArrayList<Integer>> oldWord = posting.get(wordInd);
	                                if (!oldWord.containsKey(docID)) {
	                                	ArrayList<Integer> newDoc = new ArrayList<>();
	                                	newDoc.add(pos);
	                                	oldWord.put(docID, newDoc);
	                                } else {
	                                	oldWord.get(docID).add(pos);
	                                }
	                            }
	                        }
	                    }
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        	count++;
	        	if(count % 2000 == 0) {
	        		double d = (double)count/numFiles;
	        		String s = String.format("%.2f",d * 100);
	        		System.out.println("--" + s + "%");
	        	}
            }
        }
        System.out.println("-finished");
    }

    private String processLine(String eachLine) {
//    	to lower, remove punc, keep decimal
//    	match '1234.abc', 'abc.1123', '1234.', '.1234', keep '1234.1234'
        eachLine = eachLine.toLowerCase();
        String regex = ",|\"|\\?|\\[|\\]|'|\\{|\\}|:|;|\\(|\\)|(?<!\\d)\\.|\\.(?!\\d)";
        eachLine = eachLine.replaceAll(regex, " ");
        return eachLine;
    }

    private ArrayList<String> splitQuery(String query) {
        ArrayList<String> result = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(processLine(query));
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            result.add(word);
        }
        return result;
    }

    private int distd(String t1, String t2, String d) {
        int dist = 17;

        if(!terms.containsKey(t1) || !terms.containsKey(t2)) 
        	return dist;
    	int t1Ind = terms.get(t1);
    	int t2Ind = terms.get(t2);
        	
        int dInd = files.get(d);

        ArrayList<Integer> p = posting.get(t1Ind).get(dInd);
        ArrayList<Integer> r = posting.get(t2Ind).get(dInd);

        if (p == null || r== null) {
            return dist;
        }
        
//		Method similar to merging two sorted array O(n)      
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
        }
//        System.out.println(p);
//        System.out.println(r);
//        System.out.println(dist);
//        System.out.println("---------------");
        return dist;
    }
    
    private double[] L2Norms() {
    	System.out.println("Precomputing L2Norm of all Vd");
    	double[] arr = new double[N];
    	int count = 0;
    	for(String term : terms.keySet()) {
    		HashMap<Integer, ArrayList<Integer>> postingOfTerm = posting.get(terms.get(term));
    		for(int docID : postingOfTerm.keySet()) {
    			String docName = fileNames.get(docID);
    			arr[docID] += Math.pow(weight(term, docName), 2);
    		}
        	count++;
        	if(count % 30000 == 0) {
        		double d = (double)count/terms.size();
        		String s = String.format("%.2f",d * 100);
        		System.out.println("--" + s + "%");
        	}
    	}
    	for(int i = 0; i < N; i++) {
    		arr[i] = Math.sqrt(arr[i]);
    	}
    	System.out.println("-finished");
    	return arr;
    }

    private double qWeight(String t, ArrayList<String> qWords) {
        double count = 0;
        int size = qWords.size();
        for (int i =0; i < size; i++) {
            if (t.equals(qWords.get(i))) {
                count ++;
            }
        }
        return count;
    }
    

    

}
