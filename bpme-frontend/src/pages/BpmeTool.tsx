// import React from "react";
const API_PORT = 8080;
const API_ROUTE = `http://localhost:${API_PORT}/api/v1/bpme/files`
const supportedFileTypes = ["xml", "bpmn"]
function BpmeTool() {

  const handleFilesSubmitClick = (event: any) => {

    const target = event.target as HTMLInputElement;
    const files: FileList | null = target.files;
    if (files === null) {
      return;
    }
    Array.from(files).map((file) => console.log(file.name.split(".")[1]))
    const filteredFiles = Array.from(files).filter((file) => supportedFileTypes.includes(file.name.split(".")[1]))
    console.log(filteredFiles);

    const data = new FormData();
    filteredFiles.forEach((file) => data.append("files", file))
    data.append("files", filteredFiles[0]);
    fetch(API_ROUTE, {
      method: "POST",
      body: data,

    })

  }
  return <div className="h-screen px-16 w-100">
    <div className="px-4 py-4 flex flex-col">
      <Title />
      <label className="w-1/5 mx-auto flex cursor-pointer hover:bg-slate-600 bg-primary invalid:bg-white valid:bg-black px-2 py-4 rounded-xl">
        <input type="file" className="absolute bg-transparent" onChange={handleFilesSubmitClick} required style={{ top: "-10000px" }} multiple />
        <span className="mx-auto text-white">Submit Files</span>
      </label>
    </div>
  </div>;
}

export default BpmeTool;



function Title() {
  return <div className="px-16 mx-auto font text-h2 w-2/3 text-center my-6">Calculate Metrics in BPMN diagrams</div>;
}