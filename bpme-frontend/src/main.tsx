import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "./index.css";
import {
  createBrowserRouter,
  RouterProvider,
  Routes,
  Route,
  Outlet,
  BrowserRouter,
} from "react-router-dom";
import About from "./pages/About";
import Contact from "./pages/Contact";
import BpmeTool from "./pages/BpmeTool";
import Home from "./pages/Home";

//mallon kati me children tha kanoyme edw gia na exoume navbar kai footer

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />}>
          <Route path="/" element={<Home />} />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/tool" element={<BpmeTool />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
