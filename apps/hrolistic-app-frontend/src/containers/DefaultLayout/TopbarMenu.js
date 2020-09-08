import React, {Component} from 'react';
import {Nav, NavItem, NavLink} from 'reactstrap';
import PropTypes from 'prop-types';
import {Link} from "react-router-dom";
import navigation from "../../_nav";

const propTypes = {
    children: PropTypes.node,
};

const defaultProps = {};

class TopbarMenu extends Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            navData: null,
            activeTab: '1'
        };
    }

    toggle(tab) {
        if (this.state.activeTab !== tab) {
            this.setState({
                activeTab: tab,
            });
        }
    }

    componentWillMount() {
        if (this.props.user)
            this.setState({ navData: {items : navigation.items.filter(nav => this.props.user.username===nav.username) }});
    }

    render() {

        // eslint-disable-next-line
        const {children, ...attributes} = this.props;

        return (
            <React.Fragment>
                <Nav className="d-md-down-none" navbar>
                    <NavItem className="px-3">
                        <NavLink to="/#" className="nav-link"><i className="icon-home"></i></NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="/time" className="nav-link">Time</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <Link to="/#" className="nav-link">Expenses</Link>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">People</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">Candidates</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">Projects</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">Team</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">Reports</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">Invoices</NavLink>
                    </NavItem>
                    <NavItem className="px-3">
                        <NavLink to="#" className="nav-link">Manage</NavLink>
                    </NavItem>
                </Nav>
            </React.Fragment>
        );
    }
}

TopbarMenu.propTypes = propTypes;
TopbarMenu.defaultProps = defaultProps;

export default TopbarMenu;
