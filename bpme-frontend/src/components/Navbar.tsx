import React from "react";
import { Link } from "react-router-dom";
import { Heading1, Heading6 } from "./Headings";

function Navbar() {
  return (
    <section className="px-16 flex flex-row gap-8 py-4">
      <span className="text-black">Logo</span>
      <NavLink to="/">
        <Heading6 lightVariant={true}>Home</Heading6>
      </NavLink>
      <nav className="flex gap-4 ml-auto mr-16">
        <NavLink to="/tool">
          <Heading6 lightVariant={true}>BPME Tool</Heading6>
        </NavLink>
        <NavLink to="/about">
          <Heading6 lightVariant={true}>About</Heading6>
        </NavLink>
        <NavLink to="/contact">
          <Heading6 lightVariant={true}>Contact Us</Heading6>
        </NavLink>
      </nav>
    </section>
  );
}

function NavLink({ to, children }: { to: string; children: React.ReactNode }) {
  return (
    <Link to={to} className="text-black">
      {children}
    </Link>
  );
}

export default Navbar;
