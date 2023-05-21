import { Outlet } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

function App() {
  console.log("App");
  return (
    <div className="flex flex-col h-full">
      <Navbar />
      <hr className="bg-black-25"></hr>
      <Outlet />
      {/* <Footer /> */}
    </div>
  );
}

export default App;
