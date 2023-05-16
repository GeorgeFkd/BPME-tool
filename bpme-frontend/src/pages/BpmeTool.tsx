import { useDispatch, useSelector } from "react-redux";
import { addMetric, removeMetric, addAllMetrics, removeAllMetrics } from "../store/metricsSlice";
import { addFiles } from "../store/filesSlice";
import { RootState } from "../store/store";
import { Button, Menu, MenuButton, MenuItem, MenuList, Select, MenuItemOption } from "@chakra-ui/react";
import { ChevronDownIcon } from "@chakra-ui/icons";
import { ALL_METRICS } from "../constants/metrics";

const API_PORT = 8080;
const API_ROUTE = `http://localhost:${API_PORT}/api/v1/bpme/files`
const supportedFileTypes = ["xml", "bpmn"]
function BpmeTool() {
  const dispatch = useDispatch();
  const metrics = useSelector((state: RootState) => state.metrics.metrics)
  const files = useSelector((state: RootState) => state.files.files)
  console.log("The metrics", metrics)
  console.log("The files", files)
  const handleFilesSubmitClick = async (event: any) => {

    const target = event.target as HTMLInputElement;
    const files: FileList | null = target.files;
    if (files === null) {
      return;
    }
    Array.from(files).map((file) => console.log(file.name.split(".")[1]))
    const filteredFiles = Array.from(files).filter((file) => supportedFileTypes.includes(file.name.split(".")[1]))
    // const theobj = URL.createObjectURL(filteredFiles[0])
    dispatch(addFiles(filteredFiles))
    const data = new FormData();
    filteredFiles.forEach((file) => data.append("files", file))
    // data.append("files", filteredFiles[0]);
    // data.append("metrics", "AGD,MGD,GH,GM")
    // fetch(API_ROUTE, {
    //   method: "POST",
    //   headers: {
    //     "Access-Control-Allow-Origin": "*"
    //   },
    //   body: data

    // }).then(res =>
    //   res.json()
    // ).then(data => console.log(data))

  }
  return <div className="h-screen px-16 w-100">
    <div className="px-4 py-4 flex flex-col">
      <Title />
      <label className="w-1/5 mx-auto flex cursor-pointer hover:bg-slate-600 bg-primary invalid:bg-white valid:bg-black px-2 py-4 rounded-xl">
        <input type="file" className="absolute bg-transparent" onChange={handleFilesSubmitClick} required style={{ top: "-10000px" }} multiple />
        <span className="mx-auto text-white">Submit Files</span>
      </label>
      <div className="w-100 mt-8">
        <div className="flex w-100 justify-between">
          <div className="flex flex-col w-1/2">
            <span className="text-h4">Files Submitted: {10}</span>
            <input type="text" placeholder="Search Files" className="border-2 px-4 py-3 rounded-xl w-56" />
            <div className="flex mt-6 h-80 w-96 flex-col bg-grey-25 scroll-auto rounded-md p-4">
              Hello
            </div>

          </div>
          <div className="w-2 h-100 bg-slate-200"></div>
          <div className="w-1/2">
            <div className="flex flex-col">
              <span className="text-h4 text-center">Choose Metrics</span>
              <div className="flex gap-4 my-4 justify-around">
                {/* <MetricsMenu category="Gateways" /> */}
                <span className="mr-auto">{2} selected</span>
              </div>
              <div className="flex gap-4 my-4 justify-around">
                <span className="m-auto">Gateways</span>
                <span className="mr-auto">{2} selected</span>
              </div>
              <div className="flex gap-4 my-4 justify-around">
                <span className="m-auto">Gateways</span>
                <span className="mr-auto">{2} selected</span>
              </div>
              <div className="flex gap-4 my-4 justify-around">
                <span className="m-auto">Gateways</span>
                <span className="mr-auto">{2} selected</span>
              </div>
              <div className="flex gap-8 pt-4 my-4 ml-8 justify-center">

                <button className="bg-secondary w-1/4  py-2 rounded-md" onClick={() => dispatch(removeAllMetrics())}>Select None</button>

                <button className="bg-primary  w-1/4  py-2 rounded-md" onClick={() => dispatch(addAllMetrics())}>Select All</button>
              </div>
              <button className="bg-purple-500 mx-auto py-3 px-16 rounded-md">Calculate</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>;
}

export default BpmeTool;


function MetricsMenu({ category }: { category: string }) {



  return <Menu>
    <MenuButton as={Button} rightIcon={<ChevronDownIcon />}>
      Select Metrics
    </MenuButton>
    <MenuList>
      {ALL_METRICS.map((metric) => <MenuItemOption key={metric} value={metric}>{metric}</MenuItemOption>)}
    </MenuList>
  </Menu>
}


function Title() {
  return <div className="px-16 mx-auto font text-h2 w-2/3 text-center my-6">Calculate Metrics in BPMN diagrams</div>;
}

