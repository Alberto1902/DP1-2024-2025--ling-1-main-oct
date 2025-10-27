import React, { useState, useEffect } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarText, NavbarToggler, Collapse, Dropdown, DropdownToggle, DropdownMenu, DropdownItem, Button, Modal, ModalHeader, ModalBody } from 'reactstrap';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import 'bootstrap/dist/css/bootstrap.min.css';
import './static/css/profile/pfp.css'
import { useLocation, useNavigate } from 'react-router-dom';
import { FaUsers, FaTrophy, FaChartBar, FaGamepad, FaUserFriends } from "react-icons/fa";
import Logout from "./auth/logout";


function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const toggleDropdown = () => setDropdownOpen(!dropdownOpen);
    const [user, setUser] = useState({});
    const colorPfp = user.colorTheme;
    const location = useLocation();
    const navigate = useNavigate();
    const [logoutModalOpen, setlogoutModalOpen] = useState(false);


    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => {
        if (jwt) {
            const decodedToken = jwt_decode(jwt);
            setRoles(decodedToken.authorities);
            setUsername(decodedToken.sub);
            fetch(`/api/v1/users/${decodedToken.sub}`, {
                headers: { 'Authorization': `Bearer ${jwt}` }
            })
                .then(response => response.json())
                .then(data => {
                    setUser(data);
                })
                .catch(error => {
                    console.error("There was an error fetching the user data!", error);
                });
        }
    }, [jwt])


    let adminLinks = <></>;
    let ownerLinks = <></>;
    let userLinks = <></>;
    let publicLinks = <></>;

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    <NavItem>
                        <NavLink className="nav-link-box" style={{ color: "black" }} tag={Link} to="/users"><FaUsers style={{ marginRight: "8px" }} /> Users</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink className="nav-link-box" style={{ color: "black" }} tag={Link} to="/achievementsAdmin">                        <FaTrophy style={{ marginRight: "8px" }} /> Achievements
                        </NavLink>
                    </NavItem>
                </>
            )
        }
        if (role === "PLAYER") {
            ownerLinks = (
                <>
                    <NavItem>
                        <NavLink className="nav-link-box" style={{ color: "black" }} tag={Link} to="/social">                        <FaUserFriends style={{ marginRight: "8px" }} /> Social
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink className="nav-link-box" style={{ color: "black" }} tag={Link} to="/statistics">                       <FaChartBar style={{ marginRight: "8px" }} /> Statistics
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink className="nav-link-box" style={{ color: "black" }} tag={Link} to="/achievements">                        <FaTrophy style={{ marginRight: "8px" }} /> Achievements
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink className="nav-link-box" style={{ color: "black" }} tag={Link} to="/mygames">                        <FaGamepad style={{ marginRight: "8px" }} /> My Games
                        </NavLink>
                    </NavItem>
                </>
            )
        }
    })

    function togglelogoutModal() {
        setlogoutModalOpen(!logoutModalOpen);
    }

    const handleProfileClick = () => {
        navigate('/profile');
        setDropdownOpen(false);
    };


    if (location.pathname.match(/\/gamesessions\/\d+/)) {
        return null;
    }

    return (
        <div>
            {location.pathname.match(/\/gamesessions\/\d+/) && <Button color="primary" onClick={toggleNavbar} style={{ marginBottom: '1rem' }}>Toggle</Button>}
            <Navbar expand="md" light color="light">
                <NavbarBrand tag={Link} to="/">
                    <img alt="logo" src="/logo1-recortado.png" className="png-glow"

                        style={{ height: 60, width: 160 }} />
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {userLinks}
                        {adminLinks}
                        {ownerLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {jwt && (
                            <Dropdown nav isOpen={dropdownOpen} toggle={toggleDropdown}>
                                <div

                                    style={{
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        marginRight: '1vh',
                                    }}
                                >
                                    <div

                                        style={{
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            backgroundColor: colorPfp,
                                            borderRadius: '50%',
                                            width: '8vh',
                                            height: '8vh',
                                            boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.3)',
                                            overflow: 'hidden',
                                            border: '2px solid white',
                                        }}
                                    >
                                        <img
                                            src={user?.profilePictureUrl || '/napoleonColores.png'}
                                            alt="User Icon"
                                            style={{
                                                width: '100%',
                                                height: '100%',
                                                objectFit: 'cover',
                                            }}
                                        />
                                    </div>

                                    <DropdownToggle
                                        nav
                                        style={{
                                            marginTop: '-1vh',
                                            backgroundColor: 'rgba(0, 0, 0, 0.81)',
                                            color: '#fff',
                                            padding: '4px 20px',
                                            borderRadius: '8px',
                                            fontSize: '0.9rem',
                                            textAlign: 'center',
                                            boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.3)',
                                            border: 'none',
                                            cursor: 'pointer',
                                        }}
                                        caret
                                    >
                                        {username}
                                    </DropdownToggle>
                                </div>
                                <DropdownMenu end>
                                    {roles.map((role) => (
                                        role === 'PLAYER' && (
                                            <React.Fragment key={role}>
                                                <DropdownItem tag={Link} to="/ranking" style={{ color: '#000' }}>Ranking</DropdownItem>
                                                <DropdownItem divider />
                                            </React.Fragment>
                                        )
                                    ))}
                                    <DropdownItem onClick={handleProfileClick}>
                                        Profile
                                    </DropdownItem>

                                    <DropdownItem divider />
                                    <DropdownItem onClick={togglelogoutModal}>
                                        Logout
                                    </DropdownItem>
                                    <Modal isOpen={logoutModalOpen} toggle={togglelogoutModal} centered>
                                        <ModalHeader toggle={togglelogoutModal}>Logout</ModalHeader>
                                        <ModalBody className="custom-modal-body">
                                            <Logout toggleLogoutModal={togglelogoutModal} />
                                        </ModalBody>
                                    </Modal>
                                </DropdownMenu>
                            </Dropdown>
                        )}
                    </Nav>
                </Collapse>
            </Navbar>

        </div>
    );
}

export default AppNavbar;