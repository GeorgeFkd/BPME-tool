import {createApi,fetchBaseQuery} from "@reduxjs/toolkit/query/react";

const API_PORT = 8080;


interface AnalysisResults {
    results: any;
}

export const analysisApi = createApi({
    reducerPath: 'analysisApi',
    baseQuery: fetchBaseQuery({baseUrl: 'http://localhost:8080/api/v1/bpme'}),
    endpoints: (builder) => ({
        analyzeBpmnFiles:builder.query<AnalysisResults,{files:File[],metrics:string[]}>({
            query: ({files,metrics}) => {
                const data = new FormData();
                data.append("metrics",metrics.join(","));
                files.forEach((file) => {
                    data.append("files",file);
                })
                
                return {
                url: `/files`,
                method: 'POST',
                body: data,
                multipart:true,
                formData: true,

        }}
        }),
    })
})


export const { useAnalyzeBpmnFilesQuery } = analysisApi;