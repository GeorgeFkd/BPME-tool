import { useDispatch, useSelector } from "react-redux";
import { addMetric, removeMetric, addAllMetrics, removeAllMetrics, toggleMetric } from "../store/metricsSlice";
import { addFiles, removeAllFiles, removeSpecificFile } from "../store/filesSlice";
import { RootState } from "../store/store";
import { Button, Menu, MenuButton, MenuItem, MenuList, Select, MenuItemOption, MenuOptionGroup, List, ListItem, ListIcon, Divider, FormControl } from "@chakra-ui/react";
import { ChevronDownIcon, MinusIcon, PlusSquareIcon } from "@chakra-ui/icons";
import { ALL_METRICS } from "../constants/metrics";
import { useState } from "react";
import { useAnalyzeBpmnFilesQuery } from "../api/analysis";

const API_PORT = 8080;
const API_ROUTE = `http://localhost:${API_PORT}/api/v1/bpme/files`
const supportedFileTypes = ["xml", "bpmn"]
function BpmeTool() {
  const [skip, setSkip] = useState(true);
  const dispatch = useDispatch();
  const metrics = useSelector((state: RootState) => state.metrics.metrics)
  const files = useSelector((state: RootState) => state.files.files)
  const { data, error, isLoading, isUninitialized } = useAnalyzeBpmnFilesQuery({ metrics, files }, { skip })

  console.log("The metrics", metrics)
  console.log("The files", files)
  console.log("FETCHING", data, error, isLoading, isUninitialized)


  const handleCalculateBtn = () => {
    setSkip(false)
    // dispatch(removeAllMetrics())
    // dispatch(removeAllFiles())
  }

  return <div className="h-screen px-16 w-100">
    <div className="px-4 py-4 flex flex-col">
      <Title />
      <SubmitFilesBtn />

      <div className="w-100 mt-8">
        <div className="flex w-100 justify-between">
          <div className="flex flex-col w-1/2">
            <span className="text-h4">Files Submitted: {files.length}</span>
            <SearchComponent />
            <FilesSubmittedPanel />
          </div>
          <div className="w-2 h-100 bg-slate-200"></div>
          <div className="w-1/2">
            <div className="flex flex-col">


              <MetricsMenu />

              <DisplaySelectedMetrics />
              <div className="flex gap-8 pt-4 my-4 ml-8 justify-center">

                <button className="bg-secondary w-1/4  py-2 rounded-md" onClick={() => dispatch(removeAllMetrics())}>Select None</button>

                <button className="bg-primary  w-1/4  py-2 rounded-md" onClick={() => dispatch(addAllMetrics())}>Select All</button>
              </div>
              <button className="bg-purple-500 mx-auto py-3 px-16 rounded-md" onClick={handleCalculateBtn}>Calculate</button>
            </div>
          </div>
        </div>
      </div>
      {JSON.stringify(data)}
    </div>
  </div>;
}




export default BpmeTool;


function SearchComponent() {
  return <input type="text" placeholder="Search Files" className="border-2 px-4 py-3 rounded-xl w-56" />
}



function DisplaySelectedMetrics() {
  const metrics = useSelector((state: RootState) => state.metrics.metrics)

  return <div className="px-4">
    {metrics.map((metric, index) => <div key={metric} className="flex flex-col justify-between gap-2">
      <MetricLabel metric={metric} />
      <Divider />
    </div>)}
  </div>
}



function FilesSubmittedPanel() {
  const dispatch = useDispatch();
  const files = useSelector((state: RootState) => state.files.files)
  return <div className="flex mt-6 h-80 w-96 flex-col bg-grey-25 scroll-auto rounded-md p-4 overflow-auto">
    <List spacing={4}>

      {files.map((file, index) => <>
        <ListItem key={file.name} className="flex justify-between items-center text-h5 hover:bg-grey-50 py-2 px-2" onClick={() => dispatch(removeSpecificFile(file.name))}>{file.name}
          <ListIcon as={MinusIcon} color="red.500" className="mr-8" />
        </ListItem>
        <Divider />
      </>)}
    </List>
  </div>
}



function SubmitFilesBtn() {
  const dispatch = useDispatch();
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


  }
  return <label className="w-1/5 mx-auto flex cursor-pointer hover:bg-slate-600 bg-primary invalid:bg-white valid:bg-black px-1 py-3 rounded-2xl">
    <input type="file" className="absolute bg-transparent" onChange={handleFilesSubmitClick} required style={{ top: "-10000px" }} multiple />
    <span className="mx-auto text-white text-h5">Submit Files</span>
  </label>
}



function MetricLabel({ metric }: { metric: string }) {

  return <span className="text-h5">{metric}</span>

}


function MetricsMenu() {
  const dispatch = useDispatch();
  const metrics = useSelector((state: RootState) => state.metrics.metrics)

  function isAlreadySelected(metric: string) {
    return metrics.includes(metric)
  }
  // TODO SYNC THIS WITH THE REDUX STORE
  // i can also group those metrics by category
  return <Menu closeOnSelect={false}>
    {({ forceUpdate }) =>
    (<>


      <MenuButton as={Button} rightIcon={<ChevronDownIcon />} className="text-h4 text-center mx-auto">
        Select Metrics
      </MenuButton>
      <MenuList>
        <MenuOptionGroup title="metrics" type="checkbox">

          {ALL_METRICS.map((metric) => <MenuItemOption key={metric} value={metric} onClick={() => dispatch(toggleMetric(metric))}>{metric}</MenuItemOption>)}
        </MenuOptionGroup>
      </MenuList>

    </>)
    }

  </Menu>
}


function Title() {
  return <div className="px-16 mx-auto font text-h2 w-2/3 text-center my-6">Calculate Metrics in BPMN diagrams</div>;
}

