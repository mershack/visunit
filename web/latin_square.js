/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


            function createGroups(conditions, vd, dd, td){ 
              
                //vd = visual design; dd = data design; td = task design
             
 
                //conditions have: viewer, dataset, task
                var equal = function(c1, c2){
                    if (vd.startsWith("B") && c1.viewer !== c2.viewer)
                        return false;
                    if (dd.startsWith("B") && c1.dataset !== c2.dataset)
                        return false;
                    if (td.startsWith("B") && c1.task !== c2.task)
                        return false;
                    return true;
                }
                
                var eg = []; //experimental groups
                
                for (var i=0; i<conditions.length; i++){
                    var index = -1;
                    for (var j=0; j<eg.length; j++)
                        if (equal(conditions[i], eg[j][0]))
                            index = j;
                    if (index >= 0) eg[index].push(conditions[i]);
                    else eg.push([conditions[i]]);
                }   
                
                
                
                //verify
                var str = "";
                for (var i=0; i<eg.length; i++){
                    for (var j=0; j<eg[i].length; j++)
                        str += eg[i][j].viewer + "x" + eg[i][j].dataset + ", " + eg[i][j].task;
                    str += "; ";
                }                
    
                
                //within each experimental group define 
                var equal2 = function(c1,c2){                  
                    if (!vd.endsWith("r)") && c1.viewer !== c2.viewer)  //!within (balanced)
                        return false;
                    if (!dd.endsWith("r)") && c1.dataset !== c2.dataset)
                        return false;
                    if (!td.endsWith("r)") && c1.task !== c2.task)
                        return false;
                    return true;
                }
                
                
                var final = [];
                for (var i=0; i<eg.length; i++){
                    var cs = [];
                    var eg2 = [];
                    for (var j=0; j<eg[i].length; j++){
                        var index = -1;
                        for (var k=0; k<cs.length; k++)
                            if (equal2(eg[i][j], cs[k][0]))
                                index = k;
                        if (index >= 0) cs[index].push(eg[i][j]);
                        else cs.push([eg[i][j]]);                    
                    }                    
                    
                    //latin square over the indices of cs
                    
                    var latinsq = latinSquare(cs.length);
                    if (latinsq == null){
                        alert("Too many possible orderings in this design. \nPlease adjust your choices (e.g., choose between user designs)");
                        return [];
                    }
                    for (var j=0; j<latinsq.length; j++){
                        var ordering = [];
                        for (var k=0; k<latinsq[j].length; k++){
                            for (var l=0; l<cs[latinsq[j][k]-1].length; l++)
                                ordering.push(cs[latinsq[j][k]-1][l]);
                        }
                        eg2.push(ordering);
                    }
                    final.push(eg2);
                }
                //test
                str = "";
                for (var i=0; i<final.length; i++){
                    str += "Experimental group " + i + ": ";
                    
                    for (var j=0; j<final[i].length; j++){
                        for (var k=0; k<final[i][j].length; k++){
                            str += final[i][j][k].viewer + "x" + final[i][j][k].dataset;
                            if (k != final[i][j].length-1) str+=", ";
                        }
                        if (j != final[i].length-1)
                            str += ";"
                    }
                    str += "\n";
                }               
                
                return final;
            }
            
            function latinSquare(n){
                if (n==1)
                    return [[1]];
                if (n==2)
                    return [[1,2],
                            [2,1]];
                if (n == 3)
                    return [[2,1,3],
                            [1,3,2],
                            [3,2,1],
                            [3,1,2],
                            [2,3,1],
                            [1,2,3]];

                    
                if (n == 4)
                    return [[3,1,2,4],
                            [2,3,4,1],
                            [4,2,1,3],
                            [1,4,3,2]];
                if (n == 5)
                    return [[2,3,4,1,5],
                            [4,1,3,5,2],
                            [3,5,1,2,4],
                            [1,2,5,4,3],
                            [5,4,2,3,1],
                            [5,1,4,3,2],
                            [2,5,3,1,4],
                            [4,2,1,5,3],
                            [3,4,5,2,1],
                            [1,3,2,4,5]]
                    
                return null;
        
            }
            
 