import type {AnalysisResults} from "../api/analysis"


export function analyzeResultsAndOutputFileForUser(results:AnalysisResults| undefined){
    const data = convertAnalysisToExcel(results);
    saveExcelToFile(data);
}



export function metricResultsToSpreadSheet(){}
export function statisticalResultsToSpreadSheet(){}


export function convertAnalysisToExcel(results:AnalysisResults| undefined){
    console.log("analysis to excel")
    if(results === undefined){
        console.log("results are undefined")
        return;
    }
    const cell_delimiter = ",";
    const line_delimiter = "\n";
    // will print the excel in two sections: metric results and statistical results
    const calculatedMetrics = results.metricResults.map((metricResult) => {
        return metricResult.results.map((result) => {
            return result.metricName;
        })
    }).flatMap((metricNames) => {
        return metricNames;
    }).filter((metricName,index,metricNames) => {
        return metricNames.indexOf(metricName) === index;
    })
    console.log(calculatedMetrics)
    const headersForMetricResults = ["Filename"].concat(calculatedMetrics);
    const metricResults = results.metricResults;
    let totalData = headersForMetricResults + line_delimiter
    for(const metricResult of metricResults){
        console.log(metricResult)
        totalData += metricResult.filename + cell_delimiter;
        for(const metricName of calculatedMetrics){
            const result = metricResult.results.find((result) => result.metricName === metricName);
            if(result === undefined){
                totalData += "N/A" + cell_delimiter;
            }else{
                totalData += result.result + cell_delimiter;
            }
        }
        totalData += line_delimiter;
    }

    
    console.log(results.statisticalResults)
    const headersForStatisticalResult = ["Metric"].concat(Object.keys(results.statisticalResults[0].statistics));
    totalData += line_delimiter + line_delimiter + headersForStatisticalResult + line_delimiter;
    for(const statisticalResult of results.statisticalResults){
        totalData += statisticalResult.metricName + cell_delimiter;
        totalData += Object.values(statisticalResult.statistics).join(cell_delimiter);
        totalData += line_delimiter;
    }
        console.log(totalData)

    
    return totalData;

}


function saveExcelToFile(data:string | undefined){
    if(data === undefined){
        console.log("data was undefined")
        return;
    }
    const blob = new Blob([data], {type: 'text/csv'});
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.setAttribute('hidden', '');
    a.setAttribute('href', url);
    a.setAttribute('download', "analysis.csv");
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}